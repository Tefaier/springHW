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
@RequestMapping("/api")
@Validated
public class BookController {
  @Autowired
  private AuthorService authorService;
  @Autowired
  private BookService bookService;
  @Autowired
  private TagService tagService;

  // authors

  @GetMapping("/authors/{id}")
  public AuthorDTO getAuthor(@NotNull @PathVariable("id") Long id) {
    return authorService.getById(id, false, false).orElseThrow();
  }

  @PostMapping(path = "/authors/add")
  public AuthorDTO createAuthor(@Valid @RequestBody AuthorRequest author) {
    return authorService.add(author);
  }

  @PutMapping("/authors/{id}")
  public AuthorDTO updateAuthor(@NotNull @PathVariable("id") Long id,
                                @Valid @RequestBody AuthorRequest update) {
    return authorService.update(id, update);
  }

  @DeleteMapping("/authors/{id}")
  public void deleteAuthor(@NotNull @PathVariable("id") Long id) {
    authorService.delete(id);
  }

  // books

  @GetMapping("/books/{id}")
  public BookDTO getBook(@NotNull @PathVariable("id") Long id) {
    return bookService.getById(id, false).orElseThrow();
  }

  @PostMapping(path = "/books/add")
  public BookDTO createBook(@Valid @RequestBody BookRequest book) {
    return bookService.add(book);
  }

  @PutMapping("/books/{id}")
  public BookDTO updateBook(@NotNull @PathVariable("id") Long id,
                         @Valid @RequestBody BookRequest update) {
    return bookService.update(id, update);
  }

  @DeleteMapping("/books/{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) {
    bookService.delete(id);
  }

  @GetMapping("/books/search")
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

  // tags

  @GetMapping("/tags/{id}")
  public TagDTO getTag(@NotNull @PathVariable("id") Long id) {
    return tagService.getById(id).orElseThrow();
  }

  @PostMapping(path = "/tags/add")
  public TagDTO createTag(@Valid @RequestBody TagRequest tag) {
    return tagService.add(tag);
  }

  @PutMapping("/tags/{id}")
  public TagDTO updateTag(@NotNull @PathVariable("id") Long id,
                            @Valid @RequestBody TagRequest update) {
    return tagService.update(id, update);
  }

  @DeleteMapping("/tags/{id}")
  public void deleteTag(@NotNull @PathVariable("id") Long id) {
    tagService.delete(id);
  }
}
