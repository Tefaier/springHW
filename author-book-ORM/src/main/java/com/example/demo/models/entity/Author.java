package com.example.demo.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "authors")
public class Author {
  @Column
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "first_name")
  @NotNull
  public String firstName;

  @Column(name = "last_name")
  @NotNull
  public String lastName;

  @OneToMany(mappedBy = "author", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  public List<Book> books = new ArrayList<>();

  protected Author () {}
}
