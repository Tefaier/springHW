package com.example.demo.models.controller;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.exceptions.BookRejectionException;
import com.example.demo.models.gateway.HttpBookServiceGateway;
import com.example.demo.models.service.BookService;
import com.example.demo.models.DTO.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

  @Autowired
  private BookService bookService;
  @Autowired
  private HttpBookServiceGateway bookServiceGateway;

  @GetMapping("/{id}")
  public BookDTO getBook(
      @NotNull @PathVariable("id") Long id,
      @RequestParam(value = "tags", required = false, defaultValue = "false") String getTags) {
    return bookService.getById(id, Boolean.parseBoolean(getTags)).orElseThrow();
  }

  @PostMapping(path = "/add")
  public BookDTO createBook(@Valid @RequestBody BookRequest book) {
    if (!bookServiceGateway.checkBookExists(
                new BookDTO(null, book.getAuthorID(), book.getTitle(), null),
                UUID.randomUUID().toString())) {
      throw new BookRejectionException("Such a book wasn't verified", new RuntimeException());
    }
    return bookService.add(book);
  }

  @PutMapping("/{id}")
  public BookDTO updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookRequest update) {
    Optional<BookDTO> bookDTO = bookService.getById(id, false);
    if (!bookServiceGateway.checkBookExists(
                new BookDTO(
                    null,
                    update.getAuthorID() == null ? bookDTO.get().authorID() : update.getAuthorID(),
                    update.getTitle() == null ? bookDTO.get().title() : update.getTitle(),
                    null),
                UUID.randomUUID().toString())) {
      throw new BookRejectionException("Such a book wasn't verified or created", new RuntimeException());
    }
    return bookService.update(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
    Optional<BookDTO> bookDTO = bookService.getById(id, false);
    if (!bookServiceGateway.checkBookExists(
            new BookDTO(null, bookDTO.get().authorID(), bookDTO.get().title(), null),
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

  @ExceptionHandler
  public ResponseEntity<Exception> nullPointerException(NullPointerException ex) {
    return ResponseEntity.status(422).body(new Exception(ex.getMessage()));
  }
}
