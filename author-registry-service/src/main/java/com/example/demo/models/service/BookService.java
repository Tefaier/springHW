package com.example.demo.models.service;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {
  private ConcurrentHashMap<Author, List<Book>> authors = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, Boolean> requestHistory = new ConcurrentHashMap<>();

  public BookService() {
    authors.putAll(BookService.defaultContent());
  }

  public void setAuthors(Map<Author, List<Book>> authors) {
    this.authors.clear();
    this.authors.putAll(authors);
    requestHistory.clear();
  }

  public boolean bookExists(String authorName, String authorLastname, String bookName, String requestId) {
    if (requestHistory.containsKey(requestId)) return requestHistory.get(requestId);
    boolean result = authors.getOrDefault(new Author(authorName, authorLastname), new ArrayList<>()).contains(new Book(bookName));
    requestHistory.put(requestId, result);
    return result;
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
    authorSet.put(
        new Author("protocol", "protocol"),
        List.of(
            new Book("best best")
        ));
    return authorSet;
  }
}
