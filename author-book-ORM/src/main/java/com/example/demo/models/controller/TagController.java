package com.example.demo.models.controller;

import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.DTO.TagRequest;
import com.example.demo.models.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@Validated
@PreAuthorize("isAuthenticated()")
public class TagController {
  @Autowired
  private TagService tagService;

  @GetMapping("/{id}")
  public TagDTO getTag(@NotNull @PathVariable("id") Long id) {
    return tagService.getById(id).orElseThrow();
  }

  @PostMapping(path = "/add")
  @PreAuthorize("hasAuthority('ADMIN')")
  public TagDTO createTag(@Valid @RequestBody TagRequest tag) {
    return tagService.add(tag);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public TagDTO updateTag(@NotNull @PathVariable("id") Long id,
                          @Valid @RequestBody TagRequest update) {
    return tagService.update(id, update);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public void deleteTag(@NotNull @PathVariable("id") Long id) {
    tagService.delete(id);
  }
}
