package com.example.demo.models.repository;

import com.example.demo.models.entity.Author;
import com.example.demo.models.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
  @Query("from Author a left join fetch a.books as b left join fetch b.tags")
  List<Author> findAllWithBooksTags();

  @Query("from Author a left join fetch a.books as b left join fetch b.tags where a.id = :id")
  Optional<Author> findByIDWithBooksTags(Long id);

  Optional<Author> findByUsername(String username);
}
