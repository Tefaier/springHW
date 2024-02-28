package com.example.demo.models.controller;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.service.AuthorService;
import com.example.demo.models.service.BookService;
import com.example.demo.models.entity.Book;
import com.example.demo.models.DTO.BookRequest;
import com.example.demo.models.DTO.TagRequest;
import com.example.demo.models.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
  @Autowired
  private BookService bookService;

  @GetMapping("/{id}")
  public BookDTO getBook(
      @NotNull @PathVariable("id") Long id,
      @RequestParam(value = "tags", required = false, defaultValue = "false") String getTags) {
    return bookService.getById(id, Boolean.parseBoolean(getTags)).orElseThrow();
  }

  @PostMapping(path = "/add")
  public BookDTO createBook(@Valid @RequestBody BookRequest book) {
    return bookService.add(book);
  }

  @PutMapping("/{id}")
  public BookDTO updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookRequest update) {
    return bookService.update(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
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
}
