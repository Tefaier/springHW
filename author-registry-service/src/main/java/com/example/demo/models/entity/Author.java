package com.example.demo.models.entity;

import java.util.List;
import java.util.Objects;

public class Author {
  private final String name;
  private final String lastName;

  public Author(String name, String lastName) {
    this.name = name;
    this.lastName = lastName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Author)
      return Objects.equals(name, ((Author) obj).name) && Objects.equals(lastName, ((Author) obj).lastName);
    return false;
  }

  @Override
  public int hashCode() {
    return (name + lastName).hashCode();
  }
}
