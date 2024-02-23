package com.example.demo.models.DTO;

import jakarta.validation.constraints.Min;

import java.util.HashSet;
import java.util.Set;

public class BookRequest {
  private Long authorID;
  @Min(1)
  private String title;
  private Set<Long> tags;

  public BookRequest(Long authorID, String title, Set<Long> tags) {
    this.authorID = authorID;
    this.title = title;
    this.tags = tags;
  }

  public Long getAuthorID() {
    return authorID;
  }

  public String getTitle() {
    return title;
  }

  public Set<Long> getTags() {
    if (tags == null) {
      return new HashSet<>();
    }
    return tags;
  }
}
