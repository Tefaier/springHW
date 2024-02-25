package com.example.demo.models.DTO;

import com.example.demo.models.entity.ChangeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

public class BookRequest {
  private Long authorID;
  @Size(min = 1, message = "Имя книги не может быть пустым")
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
