package com.example.demo.models.controller;

import com.example.demo.models.DBSuite;
import com.example.demo.models.DTO.*;
import com.example.demo.models.enums.ChangeType;
import com.example.demo.models.enums.BuyStatus;
import com.example.demo.models.gateway.BookRatingService;
import com.example.demo.models.gateway.HttpBookServiceGateway;
import com.example.demo.models.repository.OutboxRepository;
import com.example.demo.models.service.AuthorService;
import com.example.demo.models.service.BookService;
import com.example.demo.models.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
        "author-registry.mode=stub",
        "book-rating.mode=stub",
        "book-purchase.mode=stub"
    }
)
class AuthorBookTagControllerTest extends DBSuite {

  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private AuthorService authorService;
  @Autowired
  private BookService bookService;
  @Autowired
  private TagService tagService;
  @MockBean
  private OutboxRepository outboxRepository;
  @MockBean
  private HttpBookServiceGateway bookServiceGateway;
  @MockBean
  private BookRatingService bookRatingService;

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

  private void buyBookRequest(Long id) {
    rest.getForEntity("/api/books/buy/{bookId}", null, Map.of("bookId", id));
  }

  private void ratingBookRequest(Long id) {
    rest.postForEntity(
        "/api/books/{bookId}:checkRating",
        null,
        null,
        Map.of("bookId", id)
    );
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
    when(bookServiceGateway.checkBookExists(any(), any())).thenReturn(true);

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
    when(bookServiceGateway.checkBookExists(any(), any())).thenReturn(true);

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
    when(bookServiceGateway.checkBookExists(any(), any())).thenReturn(true);

    var createAuthorRequest1 = createAuthorRequest(new AuthorRequest("Tefaier", "The great"));
    AuthorDTO authorDTO1 = createAuthorRequest1.getBody();
    var createAuthorRequest2 = createAuthorRequest(new AuthorRequest("Desh", "Not so great"));
    AuthorDTO authorDTO2 = createAuthorRequest2.getBody();

    var createBookResponse = createBookRequest(new BookRequest(authorDTO1.getId(), "protocol", null));
    BookDTO bookDTO = createBookResponse.getBody();

    var createTagResponse = createTagRequest(new TagRequest("modern"));
    TagDTO tagDTO = createTagResponse.getBody();

    // author update of first name, second name
    var updateAuthorRequest = updateAuthorRequest(authorDTO1.getId(), new AuthorRequest("Tefaier v2", "Super great"));
    assertTrue(updateAuthorRequest.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateAuthorRequest.getStatusCode());
    var authorDTO1_2 = getAuthorRequest(authorDTO1.getId(), false, false).getBody();
    assertEquals("Tefaier v2", authorDTO1_2.getFirstName());
    assertEquals("Super great", authorDTO1_2.getLastName());

    // book update of author, title, tag add
    var updateBookRequest = updateBookRequest(bookDTO.id(), new BookRequest(authorDTO2.getId(), "protocol v2", Map.of(tagDTO.getId(), ChangeType.Add)));
    assertTrue(updateBookRequest.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateBookRequest.getStatusCode());
    var bookDTO_2 = getBookRequest(bookDTO.id(), true).getBody();
    assertEquals(authorDTO2.getId(), bookDTO_2.authorID());
    assertEquals("protocol v2", bookDTO_2.title());
    assertEquals(1, bookDTO_2.tags().size());
    assertEquals(0, getAuthorRequest(authorDTO1.getId(), true, false).getBody().getBooks().size());

    // book update tag remove
    updateBookRequest(bookDTO.id(), new BookRequest(null, null, Map.of(tagDTO.getId(), ChangeType.Remove)));
    assertEquals(0, getBookRequest(bookDTO.id(), true).getBody().tags().size());

    // tag update
    var updateTagRequest = updateTagRequest(tagDTO.getId(), new TagRequest("future"));
    assertTrue(updateTagRequest.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateTagRequest.getStatusCode());
    var tagDTO_2 = getTagRequest(tagDTO.getId()).getBody();
    assertEquals("future", tagDTO_2.getName());
  }

  @Test
  void bookRegistryGatewayInteractionTest() {
    var createAuthorRequest1 = createAuthorRequest(new AuthorRequest("Name1", "Surname1"));
    AuthorDTO authorDTO1 = createAuthorRequest1.getBody();
    var createAuthorRequest2 = createAuthorRequest(new AuthorRequest("Name2", "Surname2"));
    AuthorDTO authorDTO2 = createAuthorRequest2.getBody();

    // book1 for author1, book2 for author2
    when(bookServiceGateway.checkBookExists(
        refEq(new BookDTO(null, authorDTO1.getId(), "book1", 0f, null, BuyStatus.NotBought), "id", "tags", "rating"),
        any())
    ).thenReturn(true);
    when(bookServiceGateway.checkBookExists(
        refEq(new BookDTO(null, authorDTO2.getId(), "book2", 0f, null, BuyStatus.NotBought), "id", "tags", "rating"),
        any())
    ).thenReturn(true);

    // limit on creation test
    var createBookResponseFail = createBookRequest(new BookRequest(authorDTO1.getId(), "book4", null));
    assertTrue(createBookResponseFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + createBookResponseFail.getStatusCode());
    var createBookResponse = createBookRequest(new BookRequest(authorDTO1.getId(), "book1", null));
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO bookDTO = createBookResponse.getBody();

    // limit on update test
    var updateBookRequestFail = updateBookRequest(bookDTO.id(), new BookRequest(authorDTO2.getId(), null, null));
    assertTrue(updateBookRequestFail.getStatusCode().is4xxClientError(), "Unexpected status code: " + updateBookRequestFail.getStatusCode());
    var updateBookRequest = updateBookRequest(bookDTO.id(), new BookRequest(authorDTO2.getId(), "book2", null));
    assertTrue(updateBookRequest.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + updateBookRequest.getStatusCode());

    // limit on delete
    Mockito.reset(bookServiceGateway);
    when(bookServiceGateway.checkBookExists(any(), any())).thenReturn(false);
    deleteBookRequest(bookDTO.id());
    var getBookResponse = getBookRequest(bookDTO.id(), false);
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());
    assertEquals("book2", getBookResponse.getBody().title());
  }

  @Test
  void bookRatingRequestCheck() {
    ratingBookRequest(10L);
    verify(bookRatingService).checkRating(eq(10L));
  }

  @Test
  void bookBuyRequestCheck() throws InterruptedException {
    when(bookServiceGateway.checkBookExists(any(), any())).thenReturn(true);

    var createAuthorRequest = createAuthorRequest(new AuthorRequest("Tefaier", "The great"));
    AuthorDTO authorDTO = createAuthorRequest.getBody();
    var createBookResponse = createBookRequest(new BookRequest(authorDTO.getId(), "protocol", null));
    BookDTO bookDTO = createBookResponse.getBody();

    buyBookRequest(bookDTO.id());
    verify(outboxRepository).save(any());
    var getBookResponse = getBookRequest(bookDTO.id(), false);
    assertEquals(BuyStatus.PendingTransaction, getBookResponse.getBody().status());
  }
}