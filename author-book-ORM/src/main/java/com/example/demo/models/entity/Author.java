package com.example.demo.models.entity;

import com.example.demo.models.DTO.AuthorDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Table(name = "authors")
public class Author {
  @Column(nullable = false, updatable = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username")
  @NotNull
  private String username;

  @Column(name = "first_name")
  @NotNull
  private String firstName;

  @Column(name = "last_name")
  @NotNull
  private String lastName;

  @OneToMany(mappedBy = "author", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Book> books = new ArrayList<>();

  protected Author () {}

  public Author(String username, String firstName, String lastName, List<Book> books) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.books = books;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<Book> getBooks() {
    return books;
  }

  protected void setBooks(List<Book> books) {
    this.books = books;
  }

  public void addBook(Book book) {
    if (!books.contains(book)) {
      this.books.add(book);
    }
  }

  public void removeBook(Long bookID) {
    this.books.removeIf(book -> Objects.equals(book.getId(), bookID));
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Author author = (Author) o;
    return getId() != null && Objects.equals(getId(), author.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public static AuthorDTO getDTO(Author author, boolean withBooks, boolean withTags) {
    return new AuthorDTO(
        author.getId(),
        author.getFirstName(),
        author.getLastName(),
        withBooks ?
            author.getBooks().stream().map(book -> Book.getDTO(book, withTags)).toList() :
            new ArrayList<>()
    );
  }
}
