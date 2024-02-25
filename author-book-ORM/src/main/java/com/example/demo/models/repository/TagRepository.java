package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String tagName);
}
