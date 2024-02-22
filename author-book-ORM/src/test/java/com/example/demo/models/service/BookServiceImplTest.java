package com.example.demo.models.service;

import com.example.demo.models.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BookServiceImpl.class, BookRepositoryInMemory.class})
class BookServiceImplTest {
  @Autowired
  private BookService bookService;

  @BeforeEach
  void cleanInfo() {
    bookService.getAll().forEach(book -> bookService.delete(book));
  }

  @Test
  void simpleCommandTest() {
    Book book1 = new Book("Smth", "meows", Set.of("at", "you"));
    Book book2 = new Book("Smth", "thinks", Set.of("about", "you"));
    Book book3 = new Book("Smth", "looks", Set.of("after", "you"));

    assertDoesNotThrow(() -> bookService.add(book1));
    assertDoesNotThrow(() -> bookService.add(book2));
    Book book3ID = bookService.add(book3);
    assertEquals(3, bookService.getAll().size());
    assertEquals(3, bookService.getWithTag("you").size());
    assertEquals(1, bookService.getWithTag("at").size());
    assertEquals(0, bookService.getWithTag("utopia").size());
    String newTitle = "doesn't look";
    Book updatedBook = bookService.update(book3ID.withTitle(newTitle));
    assertEquals(newTitle, updatedBook.title);
    assertEquals(newTitle, bookService.getById(book3ID.id).get().title);
    bookService.delete(updatedBook);
    assertTrue(bookService.getById(book3ID.id).isEmpty());
  }
}