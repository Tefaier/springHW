package com.example.demo.models.controller;

import com.example.demo.models.service.BookService;
import com.example.demo.models.entity.Book;
import com.example.demo.models.DTO.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
Создание, обновление, удаление Book (можно поправить с предыдущего урока).
Создание, обновление, удаление Author.
Создание, обновление, удаление Tag.
Связка/удаление связки между Book и Author
 */

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
  public Book createBook(@Valid @RequestBody BookRequest book) {
    return bookService.add(new Book(book.getAuthorID(), book.getTitle(), book.getTagCommands()));
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
