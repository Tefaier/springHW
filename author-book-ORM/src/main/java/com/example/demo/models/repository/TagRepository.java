package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
