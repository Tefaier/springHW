package com.example.demo.models.DTO;

import com.example.demo.models.entity.ChangeType;
import jakarta.validation.constraints.Min;

import java.util.HashMap;
import java.util.Map;

public class BookRequest {
  private Long authorID;
  @Min(1)
  private String title;
  private Map<Long, ChangeType> tagCommands;

  public BookRequest(Long authorID, String title, Map<Long, ChangeType> tagCommands) {
    this.authorID = authorID;
    this.title = title;
    this.tagCommands = tagCommands;
  }

  public Long getAuthorID() {
    return authorID;
  }

  public String getTitle() {
    return title;
  }

  public Map<Long, ChangeType> getTagCommands() {
    if (tagCommands == null) {
      return new HashMap<>();
    }
    return tagCommands;
  }
}
