package com.example.demo.models.service;

import com.example.demo.models.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
  public List<Book> getAll();
  public Optional<Book> getById(Long id);
  public List<Book> getWithTag(String tag);
  public Book add(Book book);
  public Book update(Book book);
  public void delete(Book book);
}
