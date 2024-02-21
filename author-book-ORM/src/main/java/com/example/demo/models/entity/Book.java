package com.example.demo.models.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

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

  public String getTagsString() {
    return String.join(" | ", tags);
  }

  @JsonCreator
  private Book (@JsonProperty("id") Long id,
                @JsonProperty("author") String author,
                @JsonProperty("title") String title,
                @JsonProperty("tags") Set<String> tags) {
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
