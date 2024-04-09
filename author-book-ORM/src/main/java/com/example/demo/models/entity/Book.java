package com.example.demo.models.entity;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.enums.BuyStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
  @Column(nullable = false, updatable = false)
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
  private Float rating;

  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "book_tag",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  @Fetch(FetchMode.SUBSELECT)
  private Set<Tag> tags = new HashSet<>();

  @Column(name = "buy_status")
  @Enumerated(EnumType.STRING)
  @NotNull
  private BuyStatus status;

  protected Book() {}

  public Book (Author author, String title, Float rating, Set<Tag> tags, BuyStatus status) {
    this.id = null;
    this.author = author;
    this.title = title;
    this.rating = rating;
    this.tags = tags;
    this.status = status;
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

  protected void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public void addTag(Tag tag) {
    if (!tags.contains(tag)) {
      this.tags.add(tag);
    }
  }

  public void removeTag(Long tagID) {
    this.tags.removeIf(tag -> Objects.equals(tag.getId(), tagID));
  }

  public Float getRating() {
    return rating;
  }

  public void setRating(Float rating) {
    this.rating = rating;
  }

  public BuyStatus getStatus() {
    return status;
  }

  public void setStatus(BuyStatus status) {
    this.status = status;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Book book = (Book) o;
    return getId() != null && Objects.equals(getId(), book.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public static BookDTO getDTO(Book book, boolean withTags) {
    return new BookDTO(
        book.getId(),
        book.getAuthor().getId(),
        book.getTitle(),
        book.getRating(),
        withTags ?
            book.getTags().stream().map(Tag::getDTO).collect(Collectors.toSet()) :
            new HashSet<>(),
        book.getStatus());
  }
}
