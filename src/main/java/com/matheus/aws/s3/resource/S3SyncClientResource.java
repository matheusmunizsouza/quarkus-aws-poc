package com.matheus.aws.s3.resource;

import com.matheus.aws.s3.model.FileObject;
import com.matheus.aws.s3.model.FormData;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.MultipartForm;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Path("/s3")
public class S3SyncClientResource {

  @ConfigProperty(name = "app.aws.s3.bucket.name")
  String bucketName;

  @Inject
  S3Client s3;

  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadFile(@MultipartForm FormData formData) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(formData.getData().name())
        .contentType(formData.getData().contentType())
        .build();

    PutObjectResponse putResponse = s3.putObject(putObjectRequest, RequestBody.fromFile(formData.getData().uploadedFile().toFile()));
    if (putResponse != null) {
      return Response.ok().status(Status.CREATED).build();
    } else {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("download/{objectKey}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadFile(@PathParam("objectKey") String objectKey) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();

    GetObjectResponse object = s3.getObject(getObjectRequest,
        ResponseTransformer.toOutputStream(byteArrayOutputStream));

    ResponseBuilder response = Response.ok(byteArrayOutputStream);
    response.header("Content-Disposition", "attachment;filename=" + objectKey);
    response.header("Content-Type", object.contentType());
    return response.build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<FileObject> listFiles() {
    ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
    return s3.listObjects(listRequest).contents().stream().sorted(
            Comparator.comparing(S3Object::lastModified).reversed())
        .map(FileObject::from).collect(Collectors.toList());
  }
}
