package com.example.demo.models.service;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.entity.Author;
import com.example.demo.models.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
  @Autowired
  private AuthorRepository authorRepository;

  @Override
  @Transactional
  public List<AuthorDTO> getAll(boolean withBooks, boolean withTags) {
    if (withBooks && withTags) {
      return authorRepository.findAllWithBooksTags().stream().map(author -> Author.getDTO(author, withBooks, withTags)).toList();
    }
    return authorRepository.findAll().stream().map(author -> Author.getDTO(author, withBooks, withTags)).toList();
  }

  @Override
  @Transactional
  public Optional<AuthorDTO> getById(Long id, boolean withBooks, boolean withTags) {
    if (withBooks && withTags) {
      return authorRepository.findById(id).map(value -> Author.getDTO(value, withBooks, withTags));
    }
    return authorRepository.findById(id).map(value -> Author.getDTO(value, withBooks, withTags));
  }

  @Override
  public AuthorDTO add(AuthorRequest request) {
    return Author.getDTO(authorRepository.save(new Author(request.getFirstName(), request.getLastName(), null)), false, false);
  }

  @Override
  @Transactional
  public AuthorDTO update(Long id, AuthorRequest request) {
    Author author = authorRepository.findById(id).orElseThrow();
    if (request.getFirstName() != null) {
      author.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      author.setLastName(request.getLastName());
    }
    authorRepository.save(author);
    return Author.getDTO(author, false, false);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Author author = authorRepository.findById(id).orElseThrow();
    authorRepository.delete(author);
  }
}
