package com.example.demo.models.exceptions;

public class BookRejectionException extends RuntimeException {
  public BookRejectionException(String s, RuntimeException e) {
    super(s, e);
  }
}
