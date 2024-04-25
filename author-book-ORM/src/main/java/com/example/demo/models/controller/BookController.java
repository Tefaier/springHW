package com.example.demo.models.controller;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.enums.BuyStatus;
import com.example.demo.models.exceptions.BookRejectionException;
import com.example.demo.models.gateway.BookRatingService;
import com.example.demo.models.gateway.HttpBookServiceGateway;
import com.example.demo.models.service.BookService;
import com.example.demo.models.DTO.BookRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/books")
@Validated
@PreAuthorize("isAuthenticated()")
public class BookController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
  @Autowired
  private BookService bookService;
  @Autowired
  private HttpBookServiceGateway bookServiceGateway;
  @Autowired
  private BookRatingService bookRatingService;

  @GetMapping("/{id}")
  public BookDTO getBook(
      @NotNull @PathVariable("id") Long id,
      @RequestParam(value = "tags", required = false, defaultValue = "false") String getTags) {
    return bookService.getById(id, Boolean.parseBoolean(getTags)).orElseThrow();
  }

  @PostMapping(path = "/add")
  @PreAuthorize("@accessHandler.canCreateBook(authentication, #book.getAuthorID())")
  public BookDTO createBook(@Valid @RequestBody BookRequest book) {
    if (!bookServiceGateway.checkBookExists(
                new BookDTO(null, book.getAuthorID(), book.getTitle(), 0f, null, BuyStatus.NotBought),
                UUID.randomUUID().toString())) {
      throw new BookRejectionException("Such a book wasn't verified", new RuntimeException());
    }
    return bookService.add(book);
  }

  @PutMapping("/{id}")
  @PreAuthorize("@accessHandler.canAlterBook(authentication, #id)")
  public BookDTO updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookRequest update) {
    Optional<BookDTO> bookDTO = bookService.getById(id, false);
    if (!bookServiceGateway.checkBookExists(
                new BookDTO(
                    null,
                    update.getAuthorID() == null ? bookDTO.get().authorID() : update.getAuthorID(),
                    update.getTitle() == null ? bookDTO.get().title() : update.getTitle(),
                    0f,
                    null,
                    BuyStatus.NotBought),
                UUID.randomUUID().toString())) {
      throw new BookRejectionException("Such a book wasn't verified or created", new RuntimeException());
    }
    return bookService.update(id, update);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@accessHandler.canAlterBook(authentication, #id)")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
    Optional<BookDTO> bookDTO = bookService.getById(id, false);
    if (!bookServiceGateway.checkBookExists(
            new BookDTO(null, bookDTO.get().authorID(), bookDTO.get().title(), 0f, null, BuyStatus.NotBought),
            UUID.randomUUID().toString())) {
      throw new BookRejectionException("Such a book wasn't verified or created", new RuntimeException());
    }

    bookService.delete(id);
  }

  @GetMapping("/search")
  public List<BookDTO> getBooksByTag(@Size(min=1) @RequestParam(value = "tag", required = false) String tag) {
    if (tag == null) {
      return bookService.getAll(true);
    }
    try {
      Long tagID = Long.parseLong(tag);
      return bookService.getWithTag(tagID);
    } catch (NumberFormatException e) {
      return bookService.getWithTag(tag);
    }
  }

  @PostMapping("/{bookId}:checkRating")
  public void checkRating(@PathVariable @NotNull Long bookId) {
    bookRatingService.checkRating(bookId);
  }

  @GetMapping("/buy/{bookId}")
  public void buyBook(@PathVariable @NotNull Long bookId) {
    try {
      bookService.setBuyStatus(bookId, BuyStatus.PendingTransaction);
    } catch (NoSuchElementException e) {
      LOGGER.warn("An attempt to buy non existing book: " + bookId);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Wrong status when buying book: " + bookId + " existing status: " + bookService.getById(bookId, false).get().status());
    }
  }

  @ExceptionHandler
  public ResponseEntity<Exception> nullPointerException(NullPointerException ex) {
    return ResponseEntity.status(422).body(new Exception(ex.getMessage()));
  }
}
