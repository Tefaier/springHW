package com.example.demo.models.service;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BookRequest;
import com.example.demo.models.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
  public List<BookDTO> getAll(boolean withTags);
  public Optional<BookDTO> getById(Long id, boolean withTags);
  public List<BookDTO> getWithTag(Long tagID);
  public List<BookDTO> getWithTag(String tagName);
  public BookDTO add(BookRequest request);
  public BookDTO update(Long id, BookRequest request);
  public BookDTO updateRating(Long id, Float rating);
  public void deleteAll();
  public void delete(Long id);
}
