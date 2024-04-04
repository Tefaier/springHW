package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  @Query("from Book as b inner join fetch b.tags as tag where tag.id = :tag_id")
  /*@Query(
      value = """
      SELECT book.id, book.title
      FROM books as book
      INNER JOIN book_tag as bt
      ON book.id = bt.book_id
      WHERE bt.tag_id = :tag_id
      """,
      nativeQuery = true)*/
  List<Book> findWithTag(@Param("tag_id") Long tagID);

  @Query("from Book b join fetch b.author")
  List<Book> findAllWithAuthors();


  @Query(value = "select * from books where id = :id for update", nativeQuery = true)
  Optional<Book> findByIdWithLock(Long id);
}
