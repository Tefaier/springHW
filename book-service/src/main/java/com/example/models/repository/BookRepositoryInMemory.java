package com.example.models.repository;

import com.example.models.entity.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class BookRepositoryInMemory implements BookRepository{
  private final List<Book> books = new CopyOnWriteArrayList<>();;
  private final AtomicLong idGenerator = new AtomicLong(0);

  @Override
  public List<Book> getAll() {
    return new ArrayList<>(books);
  }

  @Override
  public Optional<Book> getById(Long id) {
    return books.stream()
        .filter(item -> Objects.equals(item.id, id))
        .findFirst();
  }

  @Override
  public List<Book> getByTag(String tag) {
    return books.stream()
        .filter(item -> item.tags.contains(tag))
        .collect(Collectors.toList());
  }

  @Override
  public Book addOrReplace(Book book) {
    if (book.id == null) {
      Book newBook = book.withId(idGenerator.incrementAndGet());
      books.add(newBook);
      return newBook;
    } else {
      synchronized(this) {
        var iterator = books.listIterator();
        while (iterator.hasNext()){
          if (Objects.equals(iterator.next().id, book.id)) {
            books.set(iterator.previousIndex(), book);
            return book;
          }
        }
        throw new IllegalArgumentException();
      }
    }
  }

  @Override
  public void delete(Book book) {
    synchronized(this) {
      var iterator = books.listIterator();
      while (iterator.hasNext()){
        if (Objects.equals(iterator.next().id, book.id)) {
          books.remove(iterator.previousIndex());
          return;
        }
      }
    }
  }
}
