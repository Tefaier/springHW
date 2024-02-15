package com.example.demo.models.controller;

import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.BookCreateRequest;
import com.example.demo.models.entity.BookUpdateRequest;
import com.example.demo.models.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookControllerTest {
  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private BookService bookService;

  @BeforeEach
  void cleanInfo() {
    bookService.getAll().forEach(book -> bookService.delete(book));
  }

  @Test
  void bookAddTest() {
    ResponseEntity<Book> createBookResponse =
        rest.postForEntity("/api/books/add", new BookCreateRequest("Tefaier", "protocol", Set.of("fluff", "modern time")), Book.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Book createBookResponseBody = createBookResponse.getBody();
    assertNotNull(createBookResponseBody.id, "Book was returned without id");

    ResponseEntity<Book> getBookResponse =
        rest.getForEntity("/api/books/" + createBookResponseBody.id, Book.class);
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());
    Book getBookResponseBody = getBookResponse.getBody();
    assertEquals("Tefaier", getBookResponseBody.author);
    assertEquals("protocol", getBookResponseBody.title);
    assertEquals(Set.of("fluff", "modern time"), getBookResponseBody.tags);

    ResponseEntity<Book> updateBookResponse =
        rest.exchange("/api/books/" + createBookResponseBody.id, HttpMethod.PUT, new HttpEntity<>(new BookUpdateRequest(null, "protocol v2", Set.of("fluff", "future time"))), Book.class);
    assertTrue(updateBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateBookResponse.getStatusCode());
    Book updateBookResponseBody = updateBookResponse.getBody();
    assertEquals("protocol v2", updateBookResponseBody.title);

    ResponseEntity<List<Book>> getBooksTagResponse =
        rest.exchange("/api/books/" + "future time", HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {});
    assertTrue(getBooksTagResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBooksTagResponse.getStatusCode());
    List<Book> getBooksTagResponseBody = getBooksTagResponse.getBody();
    assertEquals(1, getBooksTagResponseBody.size());
    assertEquals("Tefaier", getBooksTagResponseBody.get(0).title);

    rest.delete("/api/books/" + createBookResponseBody.id);
    ResponseEntity<Book> getBookResponseFail =
        rest.getForEntity("/api/books/" + createBookResponseBody.id, Book.class);
    assertTrue(getBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + getBookResponseFail.getStatusCode());

  }

  @Test
  void validationTest() {
    ResponseEntity<Book> createBookResponse =
        rest.postForEntity("/api/books/add", new BookCreateRequest("Test", "test", null), Book.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Long id = createBookResponse.getBody().id;

    List<BookCreateRequest> createRequests = List.of(
        new BookCreateRequest(null, "protocol", Set.of()),
        new BookCreateRequest("", "protocol", Set.of()),
        new BookCreateRequest("Tet", null, Set.of()),
        new BookCreateRequest("Tet", "", Set.of()));
    List<BookUpdateRequest> updateRequests = List.of(
        new BookUpdateRequest("", "protocol", Set.of()),
        new BookUpdateRequest("Tet", "", Set.of()));

    for (var createRequest: createRequests) {
      ResponseEntity<Book> createBookResponseFail =
          rest.postForEntity("/api/books/add", createRequest, Book.class);
      assertTrue(createBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + createBookResponseFail.getStatusCode());
    }

    for (var updateRequest: updateRequests) {
      ResponseEntity<Book> updateBookResponseFail =
          rest.exchange("/api/books/" + id, HttpMethod.PUT, new HttpEntity<>(updateRequest), Book.class);
      assertTrue(updateBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + updateBookResponseFail.getStatusCode());
    }
  }

  @Test
  void htmlListTest() {
    String checkField = "All you need to know about lists";

    ResponseEntity<Book> createBookResponse =
        rest.postForEntity("/api/books/add", new BookCreateRequest("HTML", checkField, Set.of("Cool", "Hell")), Book.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Long id = createBookResponse.getBody().id;

    ResponseEntity<String> viewBooksResponse =
        rest.getForEntity("/books", String.class);
    assertTrue(viewBooksResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + viewBooksResponse.getStatusCode());
    String viewBooksResponseBody = viewBooksResponse.getBody();
    assertTrue(viewBooksResponseBody.contains(checkField));
  }
}