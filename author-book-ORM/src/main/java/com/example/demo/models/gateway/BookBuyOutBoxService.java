package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookBuyResult;
import com.example.demo.models.entity.OutboxRecord;
import com.example.demo.models.enums.BuyStatus;
import com.example.demo.models.repository.OutboxRepository;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@ConditionalOnProperty(value = "book-purchase.mode", havingValue = "outbox")
public class BookBuyOutBoxService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookBuyOutBoxService.class);
  @Autowired
  private BookService bookService;

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public BookBuyOutBoxService(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${topic-book-purchase-request}") String topic,
                              OutboxRepository outboxRepository,
                              ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  @Scheduled(fixedDelayString = "${outbox-delay}")
  public void processOutbox() {
    List<OutboxRecord> result = outboxRepository.findAllForUpdate();
    for (OutboxRecord outboxRecord : result) {
      CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, outboxRecord.getData());
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
    }
    outboxRepository.deleteAll(result);
  }

  @KafkaListener(topics = {"${topic-book-purchase-result}"})
  public void onBuyResultReceived(String message) {
    try {
      var result = objectMapper.readValue(message, BookBuyResult.class);
      bookService.setBuyStatus(result.bookId(), result.isApproved() ? BuyStatus.Bought : BuyStatus.Cancelled);
    } catch (RuntimeException | JsonProcessingException e) {
      LOGGER.warn("Fail while consuming bookBuyResult", e);
    }
  }
}
