package com.example.demo.models.service;

import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.DTO.TagRequest;
import com.example.demo.models.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService{
  @Autowired
  private TagRepository tagRepository;

  @Override
  public List<TagDTO> getAll() {
    return null;
  }

  @Override
  public Optional<TagDTO> getById(Long id) {
    return Optional.empty();
  }

  @Override
  public TagDTO add(TagRequest request) {
    return null;
  }

  @Override
  public TagDTO update(Long id, TagRequest request) {
    return null;
  }

  @Override
  public void delete(Long id) {

  }
}
