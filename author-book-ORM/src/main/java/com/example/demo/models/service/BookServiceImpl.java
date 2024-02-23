package com.example.demo.models.service;

import com.example.demo.models.entity.Book;
import com.example.demo.models.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Override
  public List<Book> getAll() {
    return bookRepository.findAll();
  }

  @Override
  public Optional<Book> getById(Long id) {
    return bookRepository.findById(id);
  }

  @Override
  public List<Book> getWithTag(String tag) {
    return bookRepository.getByTag(tag);
  }

  @Override
  public Book add(Book book) {
    if (book == null) { return null; }
    return bookRepository.save(book);
  }

  @Override
  @Transactional
  public Book update(Long id, BookUpdateRequest request) {
    Book book = getById(id).orElseThrow();
    if (request.getAuthor() != null) {
      book.setAuthor(request.getAuthor());
    }
    if (request.getTitle() != null) {
      book.setTitle(request.getTitle());
    }
    if (request.getTags() != null) {
      book.setTags(request.getTags());
    }
    return bookRepository.save(book);
  }

  @Override
  public void delete(Book book) {
    bookRepository.delete(book);
  }
}
