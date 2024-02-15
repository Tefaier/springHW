package com.example.demo.models.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BookUpdateRequest {
  // to accept null but not empty String
  @Size(min=1, message = "Book author can't be empty")
  private String author;
  @Size(min=1, message = "Book title can't be empty")
  private String title;
  private Set<String> tags;

  public BookUpdateRequest(String author, String title, Set<String> tags) {
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
