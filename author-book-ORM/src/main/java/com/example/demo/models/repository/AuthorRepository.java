package com.example.demo.models.repository;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
