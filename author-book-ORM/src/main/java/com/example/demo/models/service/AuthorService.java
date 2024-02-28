package com.example.demo.models.service;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BookRequest;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
  public List<AuthorDTO> getAll(boolean withBooks, boolean withTags);
  public Optional<AuthorDTO> getById(Long id, boolean withBooks, boolean withTags);
  public AuthorDTO add(AuthorRequest request);
  public AuthorDTO update(Long id, AuthorRequest request);
  public void deleteAll();
  public void delete(Long id);
}
