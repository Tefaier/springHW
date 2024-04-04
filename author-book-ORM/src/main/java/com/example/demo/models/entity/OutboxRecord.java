package com.example.demo.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "outbox")
public class OutboxRecord {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotNull
  private String data;

  protected OutboxRecord() {
  }

  public OutboxRecord(String data) {
    this.data = data;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
