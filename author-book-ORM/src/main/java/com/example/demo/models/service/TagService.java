package com.example.demo.models.service;

import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.DTO.TagRequest;

import java.util.List;
import java.util.Optional;

public interface TagService {
  public List<TagDTO> getAll();
  public Optional<TagDTO> getById(Long id);
  public Optional<TagDTO> getByName(String name);
  public TagDTO add(TagRequest request);
  public TagDTO update(Long id, TagRequest request);
  public void deleteAll();
  public void delete(Long id);
}
