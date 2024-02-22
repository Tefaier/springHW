package com.example.demo.models.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
  @Column
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "author_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Author author;

  @Column
  @NotNull(message = "Book title has to be filled")
  private String title;

  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "book_tag",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags = new HashSet<>();

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
