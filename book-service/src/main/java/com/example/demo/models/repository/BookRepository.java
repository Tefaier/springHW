package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
  public List<Book> getAll();
  public Optional<Book> getById(Long id);
  public List<Book> getByTag(String tag);
  public Book addOrReplace(Book book);
  public void delete(Book book);
}
