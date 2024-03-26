package com.example.demo.models.service;

import com.example.demo.models.DTO.BookRatingResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class BookRatingServiceImpl implements BookRatingService{
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;
  private final Random random;

  @Autowired
  public BookRatingServiceImpl(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${topic-to-send-message}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.topic = topic;
    this.random = new Random();
  }

  @Override
  public void processRequest(Long bookId) {
    try {
      String message = objectMapper.writeValueAsString(new BookRatingResult(bookId, ((float) Math.round(random.nextFloat(10) * 10)) / 10));
      CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, String.valueOf(bookId), message);
      try {
        sendResult.get(2, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Unexpected thread interruption", e);
      } catch (ExecutionException e) {
        throw new RuntimeException("Couldn't send message to Kafka", e);
      } catch (TimeoutException e) {
        throw new RuntimeException("Couldn't send message to Kafka due to timeout", e);
      }
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Json fail", e);
    }
  }
}
