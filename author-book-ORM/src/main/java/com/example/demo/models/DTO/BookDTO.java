package com.example.demo.models.DTO;

import com.example.demo.models.entity.Tag;
import com.example.demo.models.enums.BuyStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record BookDTO(Long id, Long authorID, String title, Float rating, Set<TagDTO> tags, BuyStatus status) {
  public String getTagsString() {
    return String.join(" | ", tags.stream().map(TagDTO::getName).toList());
  }
}
