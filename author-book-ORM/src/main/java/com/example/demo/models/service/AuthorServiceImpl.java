package com.example.demo.models.service;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
  @Autowired
  private AuthorRepository authorRepository;

  @Override
  public List<AuthorDTO> getAll(boolean withBooks, boolean withTags) {
    return null;
  }

  @Override
  public Optional<AuthorDTO> getById(Long id) {
    return Optional.empty();
  }

  @Override
  public AuthorDTO add(AuthorRequest request) {
    return null;
  }

  @Override
  public AuthorDTO update(Long id, AuthorRequest request) {
    return null;
  }

  @Override
  public void delete(Long id) {

  }
}
