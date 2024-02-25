package com.example.demo.models.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class TagRequest {
  @Size(min = 1, message = "Имя тега не может быть пустым")
  private String name;

  public TagRequest(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
