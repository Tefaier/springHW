package com.example.demo.models.entity;

import java.util.Objects;
import java.util.Set;

public class Book {
  public final Long id;
  public final String author;
  public final String title;
  public final Set<String> tags;

  public Book (String author, String title, Set<String> tags) {
    this.id = null;
    this.author = author;
    this.title = title;
    this.tags = tags;
  }

  private Book (Long id, String author, String title, Set<String> tags) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.tags = tags;
  }

  public Book withId(long id) {
    return new Book(id, this.author, this.title, this.tags);
  }

  public Book withAuthor(String author) {
    return new Book(this.id, author, this.title, this.tags);
  }

  public Book withTitle(String title) {
    return new Book(this.id, this.author, title, this.tags);
  }

  public Book withTags(Set<String> tags) {
    return new Book(this.id, this.author, this.title, tags);
  }

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof Book)) return false;
    return Objects.equals(((Book) obj).id, this.id);
  }
}
