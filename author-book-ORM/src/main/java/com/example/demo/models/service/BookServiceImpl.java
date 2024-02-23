package com.example.demo.models.service;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BookRequest;
import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import com.example.demo.models.repository.AuthorRepository;
import com.example.demo.models.repository.BookRepository;
import com.example.demo.models.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
  Logger logger = LoggerFactory.getLogger(BookRepository.class);
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private TagRepository tagRepository;

  @Override
  @Transactional
  public List<BookDTO> getAll(boolean withTags) {
    return bookRepository.findAll().stream().map(book -> Book.getDTO(book, withTags)).toList();
  }

  @Override
  @Transactional
  public Optional<BookDTO> getById(Long id, boolean withTags) {
    return bookRepository.findById(id).map(value -> Book.getDTO(value, withTags));
  }

  @Override
  @Transactional
  public List<BookDTO> getWithTag(Long tagID) {
    return bookRepository.findWithTag(tagID).stream().map(book -> Book.getDTO(book, true)).toList();
  }

  @Override
  public BookDTO add(BookRequest request) {
    return Book.getDTO(
        bookRepository.save(new Book(
            authorRepository.findById(request.getAuthorID()).orElseThrow(),
            request.getTitle(), null)),
        false);
  }

  @Override
  @Transactional
  public BookDTO update(Long id, BookRequest request) {
    Book book = bookRepository.findById(id).orElseThrow();
    if (request.getAuthorID() != null) {
      book.setAuthor(authorRepository.findById(request.getAuthorID()).orElseThrow());
    }
    if (request.getTitle() != null) {
      book.setTitle(request.getTitle());
    }
    for (var command : request.getTagCommands().entrySet()) {
      try {
        switch (command.getValue()) {
          case Add -> book.addTag(tagRepository.findById(command.getKey()).orElseThrow());
          case Remove -> book.removeTag(command.getKey());
        }
      } catch (NoSuchElementException e) {
        logger.warn("An attempt to add non existent tag with id " + command.getKey() + " to book " + id);
      }
    }
    bookRepository.save(book);
    return Book.getDTO(book, false);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Book book = bookRepository.findById(id).orElseThrow();
    bookRepository.delete(book);
  }
}
