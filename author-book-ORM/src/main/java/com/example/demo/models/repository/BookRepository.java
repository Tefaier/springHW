package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  @Query(
      value = """
      SELECT *
      FROM books as book
      INNER JOIN book_tag as bt
      ON book.id = bt.book_id
      WHERE bt.tag_id = :tag_id
      """,
      nativeQuery = true)
  List<Book> findWithTag(@Param("tag_id") Long tagID);
}
