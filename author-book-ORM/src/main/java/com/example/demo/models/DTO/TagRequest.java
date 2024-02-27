package com.example.demo.models.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class TagRequest {
  @Size(min = 1, message = "Имя тега не может быть пустым")
  private String name;

  @JsonCreator
  public TagRequest(@JsonProperty("name") String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
