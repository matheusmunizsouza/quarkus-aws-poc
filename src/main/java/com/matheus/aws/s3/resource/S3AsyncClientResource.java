package com.matheus.aws.s3.resource;

import com.matheus.aws.s3.model.FileObject;
import com.matheus.aws.s3.model.FormData;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.MultipartForm;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Path("/s3-async")
public class S3AsyncClientResource {

  @ConfigProperty(name = "app.aws.s3.bucket.name")
  String bucketName;

  @Inject
  S3Client s3;

  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> uploadFile(@MultipartForm FormData formData) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(formData.getData().fileName())
        .contentType(formData.getData().contentType())
        .build();

    return Uni.createFrom()
        .completionStage(CompletableFuture.supplyAsync(() -> s3.putObject(putObjectRequest,
            RequestBody.fromFile(formData.getData().uploadedFile().toFile()))))
        .onItem()
        .ignore()
        .andSwitchTo(Uni.createFrom().item(Response.created(null).build()))
        .onFailure()
        .recoverWithItem(th -> {
          th.printStackTrace();
          return Response.serverError().build();
        });
  }

  @GET
  @Path("download/{objectKey}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Uni<Response> downloadFile(@PathParam("objectKey") String objectKey) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();

    return Uni.createFrom()
        .completionStage(CompletableFuture.supplyAsync(() -> s3.getObject(getObjectRequest,
            ResponseTransformer.toOutputStream(byteArrayOutputStream))))
        .onItem()
        .transform(getObjectResponse -> Response.ok(byteArrayOutputStream)
            .header("Content-Disposition", "attachment;filename=" + objectKey)
            .header("Content-Type", getObjectResponse.contentType()).build());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<FileObject> listFiles() {
    ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
    return Multi.createFrom()
        .completionStage(CompletableFuture.supplyAsync(
            () -> s3.listObjects(listRequest).contents().stream().sorted(
                    Comparator.comparing(S3Object::lastModified).reversed()).map(FileObject::from)
                .collect(Collectors.toList())
        )).flatMap(fileObjects -> Multi.createFrom().iterable(fileObjects));
  }
}
