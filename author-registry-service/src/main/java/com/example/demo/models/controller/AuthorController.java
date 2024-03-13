package com.example.demo.models.controller;

import com.example.demo.models.entity.BooleanDTO;
import com.example.demo.models.service.BookService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class AuthorController {
  @Autowired
  private BookService bookService;

  @PostMapping("/book/exists")
  public BooleanDTO checkBookExistence(
      @NotNull @RequestParam("name") String authorName,
      @NotNull @RequestParam("lastName") String authorLastname,
      @NotNull @RequestParam("title") String bookTitle,
      @NotNull @RequestHeader("X-REQUEST-ID") String requestId
  ) {
    return new BooleanDTO(bookService.bookExists(authorName, authorLastname, bookTitle, requestId));
  }
}
