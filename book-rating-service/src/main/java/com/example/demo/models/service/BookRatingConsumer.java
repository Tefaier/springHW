package com.example.demo.models.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class BookRatingConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookRatingConsumer.class);

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BookRatingService bookRatingService;

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void processBookRatingRequest(String message, Acknowledgment acknowledgment) {
    try {
      Long parsedValue = objectMapper.readValue(message, Long.class);
      LOGGER.info("Retrieved message {}", message);
      bookRatingService.processRequest(parsedValue);
    } catch (JsonProcessingException e) {
      LOGGER.info("Unable to parse message {}", message);
    }
    acknowledgment.acknowledge();
  }
}

