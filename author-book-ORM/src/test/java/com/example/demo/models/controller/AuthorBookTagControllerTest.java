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

  private ResponseEntity<AuthorDTO> getAuthorRequest(Long id, boolean withBooks, boolean withTags) {
    return rest.getForEntity(getAuthorUrl(id, withBooks, withTags), AuthorDTO.class);
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

  private ResponseEntity<BookDTO> getBookRequest(Long id, boolean withTags) {
    return rest.getForEntity(getBookUrl(id, withTags), BookDTO.class);
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

  private String getAuthorUrl(long id, boolean withBooks, boolean withTags) {
    return UriComponentsBuilder.fromHttpUrl(rest.getRootUri() + "/api/authors/" + id)
        .queryParam("books", withBooks)
        .queryParam("tags", withTags)
        .encode()
        .toUriString();
  }

  private String getBookUrl(long id, boolean withTags) {
    return UriComponentsBuilder.fromHttpUrl(rest.getRootUri() + "/api/books/" + id)
        .queryParam("tags", withTags)
        .encode()
        .toUriString();
  }

  @BeforeEach
  void cleanInfo() {
    authorService.deleteAll();
    bookService.deleteAll();
    tagService.deleteAll();
  }

  @Test
  void addTest() {
    var createAuthorRequest = createAuthorRequest(new AuthorRequest("Tefaier", "The great"));
    assertTrue(createAuthorRequest.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createAuthorRequest.getStatusCode());
    AuthorDTO authorDTO = createAuthorRequest.getBody();
    assertNotNull(authorDTO.getId(), "Author was returned without id");
    assertEquals("Tefaier", authorDTO.getFirstName());
    assertEquals("The great", authorDTO.getLastName());

    var createBookResponse = createBookRequest(new BookRequest(authorDTO.getId(), "protocol", null));
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO bookDTO = createBookResponse.getBody();
    assertNotNull(bookDTO.id(), "Book was returned without id");
    assertEquals(authorDTO.getId(), bookDTO.authorID());
    assertEquals("protocol", bookDTO.title());
    assertEquals(1, getAuthorRequest(authorDTO.getId(), true, false).getBody().getBooks().size());

    var createTagResponse = createTagRequest(new TagRequest("modern"));
    assertTrue(createTagResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createTagResponse.getStatusCode());
    TagDTO tagDTO = createTagResponse.getBody();
    assertNotNull(tagDTO.getId(), "Tag was returned without id");
    assertEquals("modern", tagDTO.getName());
  }

  @Test
  void deleteTest() {
    var createAuthorRequest = createAuthorRequest(new AuthorRequest("Tefaier", "The great"));
    AuthorDTO authorDTO = createAuthorRequest.getBody();

    var createBookResponse = createBookRequest(new BookRequest(authorDTO.getId(), "protocol", null));
    BookDTO bookDTO = createBookResponse.getBody();

    var createTagResponse = createTagRequest(new TagRequest("modern"));
    TagDTO tagDTO = createTagResponse.getBody();

    deleteAuthorRequest(authorDTO.getId());
    assertTrue(getAuthorRequest(authorDTO.getId(), false, false).getStatusCode().is4xxClientError(), "Author was not deleted");
    assertTrue(getBookRequest(bookDTO.id(), false).getStatusCode().is4xxClientError(), "Book didn't follow author deletion");

    deleteTagRequest(tagDTO.getId());
    assertTrue(getTagRequest(tagDTO.getId()).getStatusCode().is4xxClientError(), "Tag was not deleted");

    AuthorDTO authorDTO2 = createAuthorRequest(new AuthorRequest("Tefaier", "The great")).getBody();
    BookDTO bookDTO2 = createBookRequest(new BookRequest(authorDTO2.getId(), "protocol", null)).getBody();
    deleteBookRequest(bookDTO2.id());

    AuthorDTO authorDTOEmpty = getAuthorRequest(authorDTO2.getId(), true, false).getBody();
    assertEquals(0, authorDTOEmpty.getBooks().size());
  }

  @Test
  void updateTest() {
    var createAuthorRequest1 = createAuthorRequest(new AuthorRequest("Tefaier", "The great"));
    AuthorDTO authorDTO1 = createAuthorRequest1.getBody();
    var createAuthorRequest2 = createAuthorRequest(new AuthorRequest("Desh", "Not so great"));
    AuthorDTO authorDTO2 = createAuthorRequest2.getBody();

    var createBookResponse = createBookRequest(new BookRequest(authorDTO1.getId(), "protocol", null));
    BookDTO bookDTO = createBookResponse.getBody();

    var createTagResponse = createTagRequest(new TagRequest("modern"));
    TagDTO tagDTO = createTagResponse.getBody();


  }
}