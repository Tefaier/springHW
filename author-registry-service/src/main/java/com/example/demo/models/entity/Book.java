package com.example.demo.models.entity;

import java.util.Objects;

public class Book {
  private final String title;

  public Book(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Book)
      return Objects.equals(title, ((Book) obj).title);
    return false;
  }
}
