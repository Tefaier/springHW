package com.example.demo.models.entity;

import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class BookCreateRequest {
  @NotBlank(message = "Book author has to be filled")
  private String author;
  @NotBlank(message = "Book title has to be filled")
  private String title;
  private Set<String> tags;

  public BookCreateRequest(String author, String title, Set<String> tags) {
    this.author = author;
    this.title = title;
    this.tags = tags;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<String> getTags() {
    if (tags == null) {
      return new HashSet<>();
    }
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
}
