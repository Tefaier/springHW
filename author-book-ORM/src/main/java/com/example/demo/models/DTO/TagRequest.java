package com.example.demo.models.DTO;

import jakarta.validation.constraints.Min;

public class TagRequest {
  @Min(1)
  private String name;

  public TagRequest(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
