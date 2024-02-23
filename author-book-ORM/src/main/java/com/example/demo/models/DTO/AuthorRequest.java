package com.example.demo.models.DTO;

import jakarta.validation.constraints.Min;

public class AuthorRequest {
  @Min(1)
  private String firstName;
  @Min(1)
  private String lastName;

  public AuthorRequest(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
