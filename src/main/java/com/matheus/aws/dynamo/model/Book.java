package com.matheus.aws.dynamo.model;

import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Book {

  public static final String BOOK_TABLE = "Book";
  public static final String ISBN_COLUMN = "Isbn";
  public static final String NAME_COLUMN = "Name";

  private String isbn;
  private String name;

  Book(String isbn, String name) {
    this.isbn = isbn;
    this.name = name;
  }

  public static Book from(Map<String, AttributeValue> item) {
    if (item == null || item.isEmpty()) {
      throw new IllegalArgumentException("Item is null or empty");
    }
    return new Book(item.get(ISBN_COLUMN).s(), item.get(NAME_COLUMN).s());
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Book book = (Book) o;
    return isbn.equals(book.isbn) && name.equals(book.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isbn, name);
  }

  @Override
  public String toString() {
    return "Book{" +
        "isbn='" + isbn + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
