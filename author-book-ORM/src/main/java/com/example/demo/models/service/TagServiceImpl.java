package com.example.demo.models.service;

import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.DTO.TagRequest;
import com.example.demo.models.DTO.TagDTO;
import com.example.demo.models.DTO.TagRequest;
import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.Tag;
import com.example.demo.models.repository.TagRepository;
import jakarta.transaction.Transactional;
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
    return tagRepository.findAll().stream().map(Tag::getDTO).toList();
  }

  @Override
  public Optional<TagDTO> getById(Long id) {
    return tagRepository.findById(id).map(Tag::getDTO);
  }

  @Override
  public Optional<TagDTO> getByName(String name) {
    return tagRepository.findByName(name).map(Tag::getDTO);
  }

  @Override
  public TagDTO add(TagRequest request) {
    return Tag.getDTO(tagRepository.save(new Tag(request.getName(), null)));
  }

  @Override
  @Transactional
  public TagDTO update(Long id, TagRequest request) {
    Tag tag = tagRepository.findById(id).orElseThrow();
    if (request.getName() != null) {
      tag.setName(request.getName());
    }
    tagRepository.save(tag);
    return Tag.getDTO(tag);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Tag tag = tagRepository.findById(id).orElseThrow();
    tagRepository.delete(tag);
  }
}
