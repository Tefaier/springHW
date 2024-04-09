package com.example.demo.models.service;

import com.example.demo.models.DTO.BookBuyRequest;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BookRequest;
import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.OutboxRecord;
import com.example.demo.models.enums.BuyStatus;
import com.example.demo.models.repository.AuthorRepository;
import com.example.demo.models.repository.BookRepository;
import com.example.demo.models.repository.OutboxRepository;
import com.example.demo.models.repository.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
  Logger logger = LoggerFactory.getLogger(BookRepository.class);
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private OutboxRepository outboxRepository;

  @Override
  @Transactional
  public List<BookDTO> getAll(boolean withTags) {
    return bookRepository.findAllWithAuthors().stream().map(book -> Book.getDTO(book, withTags)).toList();
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
  @Transactional
  public List<BookDTO> getWithTag(String tagName) {
    return getWithTag(tagRepository.findByName(tagName).orElseThrow().getId());
  }

  @Override
  public BookDTO add(BookRequest request) {
    return Book.getDTO(
        bookRepository.save(new Book(
            authorRepository.findById(request.getAuthorID()).orElseThrow(),
            request.getTitle(), null, null, BuyStatus.NotBought)),
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
  public BookDTO updateRating(Long id, Float rating) {
    Book book = bookRepository.findById(id).orElseThrow();
    book.setRating(rating);
    bookRepository.save(book);
    return Book.getDTO(book, false);
  }

  @Override
  public void deleteAll() {
    bookRepository.deleteAll();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Book book = bookRepository.findById(id).orElseThrow();
    bookRepository.delete(book);
  }

  @Override
  @Transactional
  public void setBuyStatus(Long id, BuyStatus status) {
    Book book = bookRepository.findByIdWithLock(id).orElseThrow();
    // check on illegal status change
    if (status == BuyStatus.PendingTransaction && book.getStatus() != BuyStatus.NotBought) throw new IllegalArgumentException("Book can't be bought");
    if (book.getStatus() == BuyStatus.Bought) throw new IllegalArgumentException("Book is already bought");

    book.setStatus(BuyStatus.PendingTransaction);
    bookRepository.save(book);
    try {
      outboxRepository.save(new OutboxRecord(objectMapper.writeValueAsString(new BookBuyRequest(UUID.randomUUID().toString(), book.getId(), 100L))));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse BookBuyRequest");
    }
  }
}
