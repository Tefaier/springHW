package com.example.demo.models.controller;

import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.BookCreateRequest;
import com.example.demo.models.entity.BookUpdateRequest;
import com.example.demo.models.service.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookControllerTest {
  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private BookService bookService;

  private String getSearchUrl() {
    return UriComponentsBuilder.fromHttpUrl(rest.getRootUri() + "/api/books/search")
        .queryParam("tag", "{tag}")
        .encode()
        .toUriString();
  }

  private ResponseEntity<Book> createRequest(BookCreateRequest bookCreateRequest) {
    return rest.postForEntity(
        "/api/books/add",
        bookCreateRequest,
        Book.class);
  }

  private ResponseEntity<Book> getRequest(Long id) {
    return rest.getForEntity("/api/books/" + id, Book.class);
  }

  private ResponseEntity<Book> updateRequest(Long id, BookUpdateRequest bookUpdateRequest) {
    return rest.exchange("/api/books/" + id, HttpMethod.PUT, new HttpEntity<>(bookUpdateRequest), Book.class);
  }

  private ResponseEntity<List<Book>> searchRequest(Map<String, String> params) {
    return rest.exchange(getSearchUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {}, params);
  }

  private void deleteRequest(Long id) {
    rest.delete("/api/books/" + id);
  }

  @BeforeEach
  void cleanInfo() {
    bookService.getAll().forEach(book -> bookService.delete(book));
  }

  @Test
  void bookAddTest() {
    var createBookResponse = createRequest(new BookCreateRequest("Tefaier", "protocol", Set.of("fluff", "modern time")));
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
    var createBookResponse = createRequest(new BookCreateRequest("Test", "test", null));
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

    for (var data: createRequests) {
      var createBookResponseFail = createRequest(data);
      assertTrue(createBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + createBookResponseFail.getStatusCode());
    }

    for (var data: updateRequests) {
      var updateBookResponseFail = updateRequest(id, data);
      assertTrue(updateBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + updateBookResponseFail.getStatusCode());
    }
  }

  @Test
  void htmlListTest() {
    String checkField = "All you need to know about lists";

    var createBookResponse = createRequest(new BookCreateRequest("HTML", checkField, Set.of("Cool", "Hell")));
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Long id = createBookResponse.getBody().id;

    ResponseEntity<String> viewBooksResponse =
        rest.getForEntity("/books", String.class);
    assertTrue(viewBooksResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + viewBooksResponse.getStatusCode());
  }
}