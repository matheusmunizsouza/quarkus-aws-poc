package com.matheus.aws.sqs;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Path("/sqs")
public class SqsResource {

  private static final Logger LOGGER = Logger.getLogger(SqsResource.class);

  @Inject
  SqsClient sqsClient;

  @ConfigProperty(name = "app.aws.sqs.queue.url")
  String queueUrl;

  @POST
  @Path("/publish")
  @Consumes
  @Produces
  public Response publish(@QueryParam("message") String message) {
    LOGGER.infov("Sending message \"{0}\"", message);
    sqsClient.sendMessage(builder -> builder.queueUrl(queueUrl).messageBody(message));
    LOGGER.infov("Message \"{0}\" sent", message);
    return Response.ok().build();
  }

  @GET
  @Path("/consumes")
  @Produces
  public Response consumer() {
    List<Message> messages = sqsClient.receiveMessage(
        m -> m.maxNumberOfMessages(10).queueUrl(queueUrl)).messages();

    return Response.ok(messages.stream()
        .map(Message::body)
        .map(s -> new String(s.getBytes(StandardCharsets.UTF_8)))
        .collect(Collectors.toList())).build();
  }
}
