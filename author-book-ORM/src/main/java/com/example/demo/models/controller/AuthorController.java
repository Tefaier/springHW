package com.example.demo.models.controller;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.service.AuthorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
@Validated
@PreAuthorize("isAuthenticated()")
public class AuthorController {
  @Autowired
  private AuthorService authorService;

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public AuthorDTO getAuthor(
      @NotNull @PathVariable("id") Long id,
      @RequestParam(value = "books", required = false, defaultValue = "false") String getBooks,
      @RequestParam(value = "tags", required = false, defaultValue = "false") String getTags) {
    return authorService.getById(id, Boolean.parseBoolean(getBooks), Boolean.parseBoolean(getTags)).orElseThrow();
  }

  @PostMapping(path = "/add")
  @PreAuthorize("hasAuthority('AUTHOR')")
  public AuthorDTO createAuthor(@Valid @RequestBody AuthorRequest author) {
    return authorService.add(
        author,
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getPrincipal)
            .map(user -> (UserDetails) user)
            .map(UserDetails::getUsername).orElseThrow()
    );
  }

  @PutMapping("/{id}")
  @PreAuthorize("@accessHandler.canAlterAuthor(authentication, #id)")
  public AuthorDTO updateAuthor(@NotNull @PathVariable("id") Long id,
                                @Valid @RequestBody AuthorRequest update) {
    return authorService.update(id, update);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@accessHandler.canAlterAuthor(authentication, #id)")
  public void deleteAuthor(@NotNull @PathVariable("id") Long id) {
    authorService.delete(id);
  }
}
