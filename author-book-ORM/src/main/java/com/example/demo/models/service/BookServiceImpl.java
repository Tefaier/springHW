package com.example.demo.models.service;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BookRequest;
import com.example.demo.models.entity.Book;
import com.example.demo.models.repository.AuthorRepository;
import com.example.demo.models.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private BookRepository bookRepository;

  @Override
  public List<BookDTO> getAll() {
    return null;
  }

  @Override
  public Optional<BookDTO> getById(Long id) {
    return Optional.empty();
  }

  @Override
  public List<BookDTO> getWithTag(Long tagID) {
    return null;
  }

  @Override
  public BookDTO add(BookRequest request) {
    return null;
  }

  @Override
  public BookDTO update(Long id, BookRequest request) {
    return null;
  }

  @Override
  public void delete(Long id) {

  }
}
