package com.example.demo.models.service;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {
  private final ConcurrentHashMap<Author, List<Book>> authors = new ConcurrentHashMap<>();

  public BookService() {
    authors.putAll(BookService.defaultContent());
  }

  public boolean bookExists(String authorName, String authorLastname, String bookName) {
    return authors.getOrDefault(new Author(authorName, authorLastname), new ArrayList<>()).contains(new Book(bookName));
  }

  private static HashMap<Author, List<Book>> defaultContent() {
    HashMap<Author, List<Book>> authorSet = new HashMap<>();
    authorSet.put(
        new Author("Tefaier", "meow"),
        List.of(
            new Book("debug"),
            new Book("also debug")
        ));
    authorSet.put(
        new Author("Jost", "debug"),
        List.of(
            new Book("book"),
            new Book("also book")
        ));
    return authorSet;
  }
}
