package com.matheus.aws.dynamo.resource;

import com.matheus.aws.dynamo.model.Book;
import com.matheus.aws.dynamo.service.BookAsyncService;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/async-books")
public class BookAsyncResource {

  @Inject
  BookAsyncService bookAsyncService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<List<Book>> getAll() {
    return bookAsyncService.findAll();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<List<Book>> add(Book book) {
    return bookAsyncService.add(book)
        .onItem().ignore().andSwitchTo(this::getAll);
  }
}
