package com.example.demo.models.controller;

import com.example.demo.models.DTO.*;
import com.example.demo.models.entity.Book;
import com.example.demo.models.service.AuthorService;
import com.example.demo.models.service.BookService;
import com.example.demo.models.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorBookTagControllerTest {
  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private AuthorService authorService;
  @Autowired
  private BookService bookService;
  @Autowired
  private TagService tagService;

  private ResponseEntity<AuthorDTO> createAuthorRequest(AuthorRequest authorRequest) {
    return rest.postForEntity(
        "/api/authors/add",
        authorRequest,
        AuthorDTO.class);
  }

  private ResponseEntity<AuthorDTO> getAuthorRequest(Long id) {
    return rest.getForEntity("/api/authors/" + id, AuthorDTO.class);
  }

  private ResponseEntity<AuthorDTO> updateAuthorRequest(Long id, AuthorRequest authorRequest) {
    return rest.exchange("/api/authors/" + id, HttpMethod.PUT, new HttpEntity<>(authorRequest), AuthorDTO.class);
  }

  private void deleteAuthorRequest(Long id) {
    rest.delete("/api/authors/" + id);
  }

  private ResponseEntity<BookDTO> createBookRequest(BookRequest bookRequest) {
    return rest.postForEntity(
        "/api/books/add",
        bookRequest,
        BookDTO.class);
  }

  private ResponseEntity<BookDTO> getBookRequest(Long id) {
    return rest.getForEntity("/api/books/" + id, BookDTO.class);
  }

  private ResponseEntity<BookDTO> updateBookRequest(Long id, BookRequest bookRequest) {
    return rest.exchange("/api/books/" + id, HttpMethod.PUT, new HttpEntity<>(bookRequest), BookDTO.class);
  }

  private void deleteBookRequest(Long id) {
    rest.delete("/api/books/" + id);
  }

  private ResponseEntity<TagDTO> createTagRequest(TagRequest tagRequest) {
    return rest.postForEntity(
        "/api/tags/add",
        tagRequest,
        TagDTO.class);
  }

  private ResponseEntity<TagDTO> getTagRequest(Long id) {
    return rest.getForEntity("/api/tags/" + id, TagDTO.class);
  }

  private ResponseEntity<TagDTO> updateTagRequest(Long id, TagRequest tagRequest) {
    return rest.exchange("/api/tags/" + id, HttpMethod.PUT, new HttpEntity<>(tagRequest), TagDTO.class);
  }

  private void deleteTagRequest(Long id) {
    rest.delete("/api/tags/" + id);
  }

  @BeforeEach
  void cleanInfo() {
    authorService.getAll(false, false).forEach(author -> authorService.delete(author.getId()));
    bookService.getAll(false).forEach(book -> bookService.delete(book.id()));
    tagService.getAll().forEach(tag -> tagService.delete(tag.getId()));
  }

  @Test
  void bookAddTest() {
    var createBookResponse = createRequest(new BookRequest("Tefaier", "protocol", Set.of("fluff", "modern time")));
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Book createBookResponseBody = createBookResponse.getBody();
    assertNotNull(createBookResponseBody.id, "Book was returned without id");

    var getBookResponse = getRequest(createBookResponseBody.id);
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());
    Book getBookResponseBody = getBookResponse.getBody();
    assertEquals("Tefaier", getBookResponseBody.author);
    assertEquals("protocol", getBookResponseBody.title);
    assertEquals(Set.of("fluff", "modern time"), getBookResponseBody.tags);

    var updateBookResponse = updateRequest(createBookResponseBody.id, new BookUpdateRequest(null, "protocol v2", Set.of("fluff", "future time")));
    assertTrue(updateBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateBookResponse.getStatusCode());
    Book updateBookResponseBody = updateBookResponse.getBody();
    assertEquals("protocol v2", updateBookResponseBody.title);

    var getBooksTagResponse = searchRequest(Map.of("tag", "future time"));
    assertTrue(getBooksTagResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBooksTagResponse.getStatusCode());
    List<Book> getBooksTagResponseBody = getBooksTagResponse.getBody();
    assertEquals(1, getBooksTagResponseBody.size());
    assertEquals("protocol v2", getBooksTagResponseBody.get(0).title);

    deleteRequest(createBookResponseBody.id);
    var getBookResponseFail = getRequest(createBookResponseBody.id);
    assertTrue(getBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + getBookResponseFail.getStatusCode());
  }

  @Test
  void validationTest() {
    var createBookResponse = createRequest(new BookRequest("Test", "test", null));
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Long id = createBookResponse.getBody().id;

    List<BookRequest> createRequests = List.of(
        new BookRequest(null, "protocol", Set.of()),
        new BookRequest("", "protocol", Set.of()),
        new BookRequest("Tet", null, Set.of()),
        new BookRequest("Tet", "", Set.of()));
    List<BookUpdateRequest> updateRequests = List.of(
        new BookUpdateRequest("", "protocol", Set.of()),
        new BookUpdateRequest("Tet", "", Set.of()));

    for (var data: createRequests) {
      var createBookResponseFail = createRequest(data);
      assertTrue(createBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + createBookResponseFail.getStatusCode());
    }

    for (var data: updateRequests) {
      var updateBookResponseFail = updateRequest(id, data);
      assertTrue(updateBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + updateBookResponseFail.getStatusCode());
    }
  }
}