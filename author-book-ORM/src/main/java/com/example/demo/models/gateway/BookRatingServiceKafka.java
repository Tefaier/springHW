package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookRatingResult;
import com.example.demo.models.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(value = "book-rating.mode", havingValue = "kafka")
public class BookRatingServiceKafka implements BookRatingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookRatingServiceKafka.class);
  @Autowired
  private BookService bookService;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;

  @Autowired
  public BookRatingServiceKafka(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${topic-to-send-message}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  public void checkRating(Long bookId) {
    try {
      String message = objectMapper.writeValueAsString(bookId);
      CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, String.valueOf(bookId), message);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Json fail", e);
    }
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void onCheckRatingReceived(String message) {
    try {
      var result = objectMapper.readValue(message, BookRatingResult.class);
      bookService.updateRating(result.bookId(), result.rating());
    } catch (RuntimeException | JsonProcessingException e) {
      LOGGER.warn("Fail while consuming ratingRequest", e);
    }
  }
}
