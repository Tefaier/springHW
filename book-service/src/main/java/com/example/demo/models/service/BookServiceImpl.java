package com.example.demo.models.service;

import com.example.demo.models.entity.Book;
import com.example.demo.models.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Override
  public List<Book> getAll() {
    return bookRepository.getAll();
  }

  @Override
  public Optional<Book> getById(Long id) {
    return bookRepository.getById(id);
  }

  @Override
  public List<Book> getWithTag(String tag) {
    return bookRepository.getByTag(tag);
  }

  @Override
  public Book add(Book book) {
    return bookRepository.addOrReplace(book);
  }

  @Override
  public Book update(Book book) {
    return bookRepository.addOrReplace(book);
  }

  @Override
  public void delete(Book book) {
    bookRepository.delete(book);
  }
}
