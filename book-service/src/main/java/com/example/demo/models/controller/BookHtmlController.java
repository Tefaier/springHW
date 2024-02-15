package com.example.demo.models.controller;

import com.example.demo.models.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class BookHtmlController {
  private final BookService bookService;

  @Autowired
  public BookHtmlController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/books")
  public String viewBooks(Model model) {
    var books = bookService.getAll();
    model.addAttribute("books", books);
    return "books";
  }
}
