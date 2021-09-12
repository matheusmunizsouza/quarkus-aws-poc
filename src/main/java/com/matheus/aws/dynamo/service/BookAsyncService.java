package com.matheus.aws.dynamo.service;

import com.matheus.aws.dynamo.model.Book;
import io.smallrye.mutiny.Uni;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

@ApplicationScoped
public class BookAsyncService {

  @Inject
  DynamoDbClient dynamoDbClient;

  public Uni<List<Book>> findAll() {
    ScanRequest scanRequest = ScanRequest.builder()
        .tableName(Book.BOOK_TABLE)
        .attributesToGet(Book.ISBN_COLUMN, Book.NAME_COLUMN).build();

    return Uni.createFrom()
        .completionStage(CompletableFuture.supplyAsync(() -> dynamoDbClient.scan(scanRequest)))
        .onItem()
        .transform(res -> res.items().stream().map(Book::from).collect(Collectors.toList()));
  }

  public Uni<List<Book>> add(Book book) {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(Book.ISBN_COLUMN, AttributeValue.builder().s(book.getIsbn()).build());
    item.put(Book.NAME_COLUMN, AttributeValue.builder().s(book.getName()).build());

    PutItemRequest putItemRequest = PutItemRequest.builder()
        .tableName(Book.BOOK_TABLE)
        .item(item)
        .build();

    return Uni.createFrom().completionStage(
            CompletableFuture.supplyAsync(() -> dynamoDbClient.putItem(putItemRequest)))
        .onItem().ignore().andSwitchTo(this::findAll);
  }
}
