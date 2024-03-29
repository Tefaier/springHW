package com.example.demo.models.gateway;

import com.example.demo.models.AuthorServiceMock;
import com.example.demo.models.DTO.BookRatingResult;
import com.example.demo.models.KafkaTestConsumer;
import com.example.demo.models.config.RestTemplateConfiguration;
import com.example.demo.models.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
    classes = {BookRatingServiceKafka.class},
    properties = {
        "topic-to-send-message=some-test-topic",
        "topic-to-consume-message=some-test-topic-response",
        "spring.kafka.consumer.group-id=some-consumer-group"
    })
@Import({KafkaAutoConfiguration.class, BookRatingServiceKafkaTest.ObjectMapperTestConfig.class})
@Testcontainers
class BookRatingServiceKafkaTest {
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @MockBean
  private BookService bookService;
  @Autowired
  private BookRatingService bookRatingService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void shouldSendMessageToKafkaSuccessfully() {
    assertDoesNotThrow(() -> bookRatingService.checkRating(30L));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          Long message = null;
          try {
            message = objectMapper.readValue(record.value(), Long.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          assertEquals(30L, message);
        }
    );
  }

  // FAILS
  @Test
  void shouldGetMessageFromKafkaSuccessfully() throws JsonProcessingException {
    kafkaTemplate.send("some-test-topic-response", objectMapper.writeValueAsString(new BookRatingResult(30L, 6.7f)));

    await().atMost(Duration.ofSeconds(5))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(
                bookService, times(1))
            .updateRating(eq(30L), eq(6.7f))
        );
  }
}