package com.example.demo.models.controller;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.DTO.AuthorRequest;
import com.example.demo.models.service.AuthorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@Validated
public class AuthorController {
  @Autowired
  private AuthorService authorService;

  @GetMapping("/{id}")
  public AuthorDTO getAuthor(@NotNull @PathVariable("id") Long id) {
    return authorService.getById(id, false, false).orElseThrow();
  }

  @PostMapping(path = "/add")
  public AuthorDTO createAuthor(@Valid @RequestBody AuthorRequest author) {
    return authorService.add(author);
  }

  @PutMapping("/{id}")
  public AuthorDTO updateAuthor(@NotNull @PathVariable("id") Long id,
                                @Valid @RequestBody AuthorRequest update) {
    return authorService.update(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteAuthor(@NotNull @PathVariable("id") Long id) {
    authorService.delete(id);
  }
}
