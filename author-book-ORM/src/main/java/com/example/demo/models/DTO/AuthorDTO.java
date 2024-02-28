package com.example.demo.models.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AuthorDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private List<BookDTO> books;

  private AuthorDTO() {
  }

  public AuthorDTO(Long id, String firstName, String lastName, List<BookDTO> books) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.books = books;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public List<BookDTO> getBooks() {
    return books;
  }
}
