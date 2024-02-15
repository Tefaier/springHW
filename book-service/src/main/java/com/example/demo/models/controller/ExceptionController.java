package com.example.demo.models.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionController {
  @ExceptionHandler
  public ResponseEntity<Exception> noSuchElementExceptionHandler(NoSuchElementException ex) {
    return ResponseEntity.status(422).body(new Exception(ex.getMessage()));
  }
}
