package com.example.models.controller;

import com.example.models.entity.Book;
import com.example.models.entity.BookCreateRequest;
import com.example.models.entity.BookUpdateRequest;
import com.example.models.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@Validated
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping(path = "/books/add")
  public Book createBook(@Valid @RequestBody BookCreateRequest book) {
    return bookService.add(new Book(book.getAuthor(), book.getTitle(), book.getTags()));
  }

  @PutMapping("/books/{id}")
  public void updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookUpdateRequest update) {
    Book book = bookService.getById(id).orElseThrow();
    if (update.getAuthor() != null) {
      book = book.withAuthor(update.getAuthor());
    }
    if (update.getTitle() != null) {
      book = book.withTitle(update.getTitle());
    }
    if (update.getTags() != null) {
      book = book.withTags(update.getTags());
    }
    bookService.update(book);
  }

  @GetMapping("/books/{id}")
  public Book getBook(@NotNull @PathVariable("id") Long id) {
    return bookService.getById(id).orElseThrow();
  }

  @GetMapping("/books/{tag}")
  public List<Book> getBooksByTag(@NotNull @PathVariable("tag") String tag) {
    return bookService.getWithTag(tag);
  }

  @DeleteMapping("/books/{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
    bookService.delete(bookService.getById(id).orElseThrow());
  }
}
