package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;

public interface BookServiceGateway {
  public Boolean checkBookExists(BookDTO bookDTO, String requestId);
}
