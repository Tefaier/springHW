package com.example.demo.models.security;

import com.example.demo.models.entity.Author;
import com.example.demo.models.repository.AuthorRepository;
import com.example.demo.models.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Component
public class AccessHandler {
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private BookService bookService;

  private Author getRelatedAuthor(Authentication authentication) {
    String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .map(user -> (UserDetails) user)
        .map(UserDetails::getUsername).orElseThrow();
    return authorRepository.findByUsername(username).orElseThrow();
  }

  private boolean isAdmin(Authentication authentication) {
    var authorities = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .map(user -> (UserDetails) user)
        .map(UserDetails::getAuthorities).orElseThrow();
    return authorities.stream().anyMatch(auth -> "ADMIN".equals(auth.getAuthority()));
  }

  public boolean canAlterAuthor(Authentication authentication, Long authorId) {
    try {
      return Objects.equals(getRelatedAuthor(authentication).getId(), authorId);
    } catch (RuntimeException e) {
      return false;
    }
  }

  public boolean canCreateBook(Authentication authentication, Long authorId) {
    try {
      return Objects.equals(getRelatedAuthor(authentication).getId(), authorId);
    } catch (RuntimeException e) {
      return false;
    }
  }

  public boolean canAlterBook(Authentication authentication, Long bookId) {
    try {
      return isAdmin(authentication) || Objects.equals(getRelatedAuthor(authentication).getId(), bookService.getById(bookId, false).orElseThrow().authorID());
    } catch (RuntimeException e) {
      return false;
    }
  }


}
