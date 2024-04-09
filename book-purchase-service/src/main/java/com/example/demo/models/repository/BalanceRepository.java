package com.example.demo.models.repository;

import com.example.demo.models.entity.Balance;
import com.example.demo.models.entity.OutboxRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
  @Query(value = "select * from balance where id = :id for update", nativeQuery = true)
  Optional<Balance> findByIdForUpdate(Long id);
}
