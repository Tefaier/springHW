package com.example.demo.models.service;

import com.example.demo.models.DTO.BookRatingResult;
import com.example.demo.models.KafkaTestConsumer;
import com.example.demo.models.ObjectMapperTestConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = {BookRatingServiceImpl.class},
    properties = {"topic-to-send-message=some-test-topic"})
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class})
@Testcontainers
class BookRatingServiceImplTest {

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private BookRatingService bookRatingService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() {
    assertDoesNotThrow(() -> bookRatingService.processRequest(30L));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          BookRatingResult message = null;
          try {
            message = objectMapper.readValue(record.value(), BookRatingResult.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          assertEquals(30L, message.bookId());
          assertTrue(message.rating() >= 0 && message.rating() <= 10);
        }
    );
  }
}