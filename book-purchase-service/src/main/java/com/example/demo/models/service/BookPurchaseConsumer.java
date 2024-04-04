package com.example.demo.models.service;

import com.example.demo.models.DTO.BookBuyRequest;
import com.example.demo.models.repository.BalanceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BookPurchaseConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookPurchaseConsumer.class);

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BalanceService balanceService;
  private Set<String> receivedRequests = new HashSet<>();

  @KafkaListener(topics = {"${topic-book-purchase-request}"})
  public void processBookPurchaseRequest(String message, Acknowledgment acknowledgment) {
    try {
      BookBuyRequest parsedValue = objectMapper.readValue(message, BookBuyRequest.class);
      LOGGER.info("Retrieved message {}", message);
      if (receivedRequests.add(parsedValue.requestId())) {
        balanceService.tryBuyBook(parsedValue.bookId(), parsedValue.price());
      } else {
        LOGGER.info("Request was already processed {}", message);
      }
    } catch (JsonProcessingException e) {
      LOGGER.info("Unable to parse message {}", message);
    }
    acknowledgment.acknowledge();
  }
}

