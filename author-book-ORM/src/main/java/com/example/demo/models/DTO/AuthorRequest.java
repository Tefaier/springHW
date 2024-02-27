package com.example.demo.models.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class AuthorRequest {
  @Size(min = 1, message = "Имя не может быть пустым")
  private String firstName;
  @Size(min = 1, message = "Фамилия не может быть пустым")
  private String lastName;

  private AuthorRequest() {

  }

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
