package com.example.demo.models.exceptions;

import org.springframework.web.client.RestClientException;

public class BookRegistryFailException extends RuntimeException {
  public BookRegistryFailException(String s, RuntimeException e) {
    super(s, e);
  }
}
