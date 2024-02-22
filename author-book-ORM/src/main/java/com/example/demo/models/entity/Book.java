package com.example.demo.models.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  public Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  public Author author;

  @Column
  @NotNull(message = "Book title has to be filled")
  public String title;

  @Column
  public Set<Tag> tags;

  protected Book() {}

  public Book (Author author, String title, Set<Tag> tags) {
    this.id = null;
    this.author = author;
    this.title = title;
    this.tags = tags;
  }

  public Long getId() {
    return id;
  }

  protected Long setId() {
    return id;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public String getTagsString() {
    return String.join(" | ", tags.stream().map(tag -> tag.name).toList());
  }

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof Book)) return false;
    return Objects.equals(((Book) obj).id, this.id);
  }
}
