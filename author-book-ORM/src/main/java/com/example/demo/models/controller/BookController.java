package com.example.demo.models.controller;

import com.example.demo.models.service.BookService;
import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.BookCreateRequest;
import com.example.demo.models.entity.BookUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
  public Book updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookUpdateRequest update) {
    return bookService.update(id, update);
  }

  @GetMapping("/books/{id}")
  public Book getBook(@NotNull @PathVariable("id") Long id) {
    return bookService.getById(id).orElseThrow();
  }

  @GetMapping("/books/search")
  public List<Book> getBooksByTag(@Size(min=1) @RequestParam(value = "tag", required = false) String tag) {
    if (tag == null) {
      return bookService.getAll();
    }
    return bookService.getWithTag(tag);
  }

  @DeleteMapping("/books/{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
    bookService.delete(bookService.getById(id).orElseThrow());
  }
}
