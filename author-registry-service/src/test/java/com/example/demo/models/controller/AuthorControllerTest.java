package com.example.demo.models.controller;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.BooleanDTO;
import com.example.demo.models.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorControllerTest {
  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private BookService bookService;

  private ResponseEntity<BooleanDTO> checkRequest(String name, String lastName, String title, String requestId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-REQUEST-ID", requestId);
    ResponseEntity<BooleanDTO> bookResponse = rest.exchange(
        "/api/book/exists?name={name}&lastName={lastName}&title={title}",
        HttpMethod.POST,
        new HttpEntity<>(headers),
        BooleanDTO.class,
        Map.of("name", name, "lastName", lastName, "title", title)
    );
    return bookResponse;
  }

  @BeforeEach
  void setData() {
    bookService.setAuthors(Map.of(
        new Author("name1", "last1"), List.of(new Book("book1"), new Book("book2")),
        new Author("name1", "last2"), List.of(new Book("book3"), new Book("book4"))
    ));
  }

  @Test
  void simpleCheck() {
    var falseResponse = checkRequest("name1", "last1", "book4", "1");
    assertTrue(falseResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + falseResponse.getStatusCode());
    assertFalse(falseResponse.getBody().value());

    var trueResponse = checkRequest("name1", "last2", "book3", "2");
    assertTrue(trueResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + trueResponse.getStatusCode());
    assertTrue(trueResponse.getBody().value());

    var sameIdResponse = checkRequest("name1", "last1", "book4", "2");
    assertTrue(sameIdResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + sameIdResponse.getStatusCode());
    assertTrue(sameIdResponse.getBody().value());
  }
}