package com.example.demo.models.repository;

import com.example.demo.models.entity.Book;
import com.example.demo.models.entity.OutboxRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxRecord, Long> {
  @Query(value = "select * from outbox for update", nativeQuery = true)
  List<OutboxRecord> findAllForUpdate();
}
