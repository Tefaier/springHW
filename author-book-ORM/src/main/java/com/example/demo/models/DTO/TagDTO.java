package com.example.demo.models.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TagDTO {
  private Long id;
  private String name;

  @JsonCreator
  public TagDTO(
      @JsonProperty("id") Long id,
      @JsonProperty("name") String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
