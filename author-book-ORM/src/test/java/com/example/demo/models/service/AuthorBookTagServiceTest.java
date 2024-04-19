package com.example.demo.models.service;

import com.example.demo.models.DTO.*;
import com.example.demo.models.DBSuite;
import com.example.demo.models.ObjectMapperTestConfig;
import com.example.demo.models.entity.ChangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    AuthorServiceImpl.class, BookServiceImpl.class, TagServiceImpl.class, ObjectMapperTestConfig.class
})
class AuthorBookTagServiceTest extends DBSuite {

  @Autowired
  private AuthorService authorService;
  @Autowired
  private BookService bookService;
  @Autowired
  private TagService tagService;

  private static final List<AuthorDTO> authors = new ArrayList<>();
  private static final List<BookDTO> books = new ArrayList<>();
  private static final List<TagDTO> tags = new ArrayList<>();

  @BeforeEach
  @Transactional
  void cleanInfo() {
    authorService.deleteAll();
    bookService.deleteAll();
    tagService.deleteAll();
    setInfo();
  }

  @Transactional
  void setInfo() {
    authors.clear();
    books.clear();
    tags.clear();

    authors.add(authorService.add(new AuthorRequest("name1", "surname1"), "test1"));
    authors.add(authorService.add(new AuthorRequest("name2", "surname2"), "test2"));

    books.add(bookService.add(new BookRequest(authors.get(0).getId(), "book1", null)));
    books.add(bookService.add(new BookRequest(authors.get(1).getId(), "book2", null)));

    tags.add(tagService.add(new TagRequest("tag1")));
    tags.add(tagService.add(new TagRequest("tag2")));
  }

  @Test
  void addUpdateDelete() {
    // assert that setInfo worked - also checks that add works
    assertEquals(authors.size(), authorService.getAll(false, false).size());
    assertEquals(books.size(), bookService.getAll(false).size());
    assertEquals(tags.size(), tagService.getAll().size());

    // simple update tests
    String debugString = "super string";
    authorService.update(authors.get(0).getId(), new AuthorRequest(debugString, null));
    assertEquals(debugString, authorService.getById(authors.get(0).getId(), false, false).orElseThrow().getFirstName());
    assertEquals(authors.get(0).getLastName(), authorService.getById(authors.get(0).getId(), false, false).orElseThrow().getLastName());

    bookService.update(books.get(0).id(), new BookRequest(null, debugString, null));
    assertEquals(debugString, bookService.getById(books.get(0).id(), false).orElseThrow().title());

    tagService.update(tags.get(0).getId(), new TagRequest(debugString));
    assertEquals(debugString, tagService.getById(tags.get(0).getId()).orElseThrow().getName());

    // delete test
    authorService.delete(authors.get(0).getId());
    tagService.delete(tags.get(0).getId());
    assertEquals(1, authorService.getAll(false, false).size());
    assertEquals(1, bookService.getAll(false).size());
    assertEquals(1, tagService.getAll().size());
  }

  @Test
  void connectionTest() {
    // change of author and connection of tags
    bookService.update(books.get(0).id(), new BookRequest(
        authors.get(1).getId(),
        null,
        Map.of(
            tags.get(0).getId(), ChangeType.Add,
            tags.get(1).getId(), ChangeType.Add)
    ));
    BookDTO newBookDTO = bookService.getById(books.get(0).id(), true).orElseThrow();
    assertEquals(authors.get(1).getId(), newBookDTO.authorID());
    assertEquals(2, newBookDTO.tags().size());
    assertEquals(0, authorService.getById(authors.get(0).getId(), false, false).orElseThrow().getBooks().size());

    // disconnection of tag
    bookService.update(books.get(0).id(), new BookRequest(
        null, null,
        Map.of(tags.get(0).getId(), ChangeType.Remove)
    ));
    assertEquals(1, bookService.getById(books.get(0).id(), true).orElseThrow().tags().size());
  }

  @Test
  void findByTagsTest() {
    bookService.update(books.get(0).id(), new BookRequest(
        null, null,
        Map.of(
            tags.get(0).getId(), ChangeType.Add,
            tags.get(1).getId(), ChangeType.Add)
    ));
    bookService.update(books.get(1).id(), new BookRequest(
        null, null,
        Map.of(tags.get(0).getId(), ChangeType.Add)
    ));

    assertEquals(2, bookService.getWithTag(tags.get(0).getId()).size());
    assertEquals(1, bookService.getWithTag(tags.get(1).getId()).size());
    assertEquals(1, bookService.getWithTag(tags.get(1).getName()).size());
  }
}