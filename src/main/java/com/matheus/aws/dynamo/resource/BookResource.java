package com.matheus.aws.dynamo.resource;

import com.matheus.aws.dynamo.model.Book;
import com.matheus.aws.dynamo.service.BookService;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/books")
public class BookResource {

  @Inject
  BookService bookService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Book> findAll() {
    return bookService.findAll();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<Book> add(Book book) {
    return bookService.add(book);
  }
}
