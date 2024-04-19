package com.example.demo.models.controller;

import com.example.demo.models.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class BookHtmlController {
  private final BookService bookService;

  @Autowired
  public BookHtmlController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/books")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String viewBooks(Model model) {
    var books = bookService.getAll(true);
    model.addAttribute("books", books);
    return "books";
  }
}
