package com.example.demo.models.controller;

import com.example.demo.models.service.BookService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class AuthorController {
  @Autowired
  private BookService bookService;

  @PostMapping("/book/exists")
  public Boolean checkBookExistence(
      @NotNull @RequestParam("name") String authorName,
      @NotNull @RequestParam("lastName") String authorLastname,
      @NotNull @RequestParam("title") String bookTitle
  ) {
    return bookService.bookExists(authorName, authorLastname, bookTitle);
  }
}
