package com.example.demo.models.service;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BookService.class})
class BookServiceTest {
  @Autowired
  private BookService bookService;

  @BeforeEach
  void setData() {
    bookService.setAuthors(Map.of(
        new Author("name1", "last1"), List.of(new Book("book1"), new Book("book2")),
        new Author("name1", "last2"), List.of(new Book("book3"), new Book("book4"))
    ));
  }

  @Test
  void checkVerification() {
    // author doesn't exist
    assertFalse(bookService.bookExists("name1", "name1", "book1", "1"));
    // book doesn't belong to author
    assertFalse(bookService.bookExists("name1", "last2", "book2", "2"));
    // works
    assertTrue(bookService.bookExists("name1", "last2", "book4", "3"));
    // requestId work
    assertTrue(bookService.bookExists("name1", "name1", "book1", "3"));
  }
}