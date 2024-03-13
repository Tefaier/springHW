package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BooleanDTO;

public interface BookServiceGateway {
  public BooleanDTO checkBookExists(BookDTO bookDTO, String requestId);
}
