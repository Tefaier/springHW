package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookBuyRequest;
import com.example.demo.models.DTO.BookBuyResult;
import com.example.demo.models.DTO.UserRegisterRequest;
import com.example.demo.models.KafkaTestConsumer;
import com.example.demo.models.ObjectMapperTestConfig;
import com.example.demo.models.entity.OutboxRecord;
import com.example.demo.models.enums.Role;
import com.example.demo.models.enums.BuyStatus;
import com.example.demo.models.repository.OutboxRepository;
import com.example.demo.models.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
    classes = {BookBuyOutBoxService.class},
    properties = {
        "topic-book-purchase-request=some-test-topic",
        "topic-book-purchase-result=some-test-topic-response",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "outbox-delay=1000"
    })
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class})
@Testcontainers
@EnableScheduling
public class BookBuyOutBoxServiceTest {

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @MockBean
  private OutboxRepository outboxRepository;
  @MockBean
  private BookService bookService;
  @Autowired
  private BookBuyOutBoxService bookBuyOutBoxService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void parseTest() throws JsonProcessingException {
    var val = objectMapper.writeValueAsString(new UserRegisterRequest("some", "pas", Set.of(Role.ADMIN)));
    System.out.println(val);
    var parsed = objectMapper.readValue(val, UserRegisterRequest.class);
    System.out.println(parsed);
  }

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws JsonProcessingException {
    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    List<OutboxRecord> recordsToReturn = List.of(
        new OutboxRecord(objectMapper.writeValueAsString(new BookBuyRequest("2", 2, 100))),
        new OutboxRecord(objectMapper.writeValueAsString(new BookBuyRequest("10",10, 200)))
    );
    when(outboxRepository.findAllForUpdate()).thenReturn(recordsToReturn);

    // request to delete
    await().atMost(Duration.ofSeconds(3))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(
                outboxRepository, times(1))
            .deleteAll(eq(recordsToReturn))
        );

    // assert valid sending
    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(2, records.count());
    List<String> result = new ArrayList<>();
    records.iterator().forEachRemaining(
        record -> {
          result.add(record.value());
        }
    );
    assertTrue(result.containsAll(recordsToReturn.stream().map(OutboxRecord::getData).toList()));
  }

  @Test
  void shouldGetMessageFromKafkaSuccessfully() throws JsonProcessingException {
    kafkaTemplate.send("some-test-topic-response", objectMapper.writeValueAsString(new BookBuyResult(10L, Boolean.FALSE)));

    await().atMost(Duration.ofSeconds(3))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(
                bookService, times(1))
            .setBuyStatus(eq(10L), eq(BuyStatus.Cancelled))
        );
  }
}
