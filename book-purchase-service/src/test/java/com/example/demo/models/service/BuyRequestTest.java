package com.example.demo.models.service;

import com.example.demo.models.DBSuite;
import com.example.demo.models.DTO.BookBuyRequest;
import com.example.demo.models.DTO.BookBuyResult;
import com.example.demo.models.KafkaTestConsumer;
import com.example.demo.models.ObjectMapperTestConfig;
import com.example.demo.models.entity.Balance;
import com.example.demo.models.entity.OutboxRecord;
import com.example.demo.models.gateway.BookBuyOutboxScheduler;
import com.example.demo.models.repository.BalanceRepository;
import com.example.demo.models.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(
    properties = {
        "topic-book-purchase-request=some-test-topic",
        "topic-book-purchase-result=some-test-topic-response",
        "spring.kafka.consumer.group-id=some-consumer-group",
        "outbox-delay=500",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    }
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class})
@Testcontainers
@EnableScheduling
public class BuyRequestTest extends DBSuite {

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private BookBuyOutboxScheduler bookBuyOutboxScheduler;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BalanceRepository balanceRepository;
  @Autowired
  private OutboxRepository outboxRepository;

  private static KafkaTestConsumer consumer;

  @BeforeAll
  static void setupKafkaConsumer() {
    consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic-response"));
  }

  @BeforeEach
  @Transactional
  void cleanInfo() {
    try {
      balanceRepository.delete(balanceRepository.findByIdForUpdate(BalanceServiceImpl.mainBalanceId).get());
    } catch (RuntimeException ignored) { }
    balanceRepository.save(new Balance(BalanceServiceImpl.mainBalanceId, 1000L));
    //outboxRepository.deleteAll();
  }

  // test message come - balance update
  // test message come with fail result - no update

  // test message send to Kafka
  @Test
  void outboxSendTest() throws JsonProcessingException, InterruptedException {
    List<OutboxRecord> recordsToReturn = List.of(
        new OutboxRecord(objectMapper.writeValueAsString(new BookBuyResult(2L, Boolean.TRUE)))
    );
    outboxRepository.saveAll(recordsToReturn);
    Thread.sleep(5000);

    // assert valid sending
    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            var value = objectMapper.readValue(record.value(), BookBuyResult.class);
            assertEquals(2L, value.bookId());
            assertTrue(value.isApproved());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );

    // assert outbox db was emptied
    assertEquals(0, outboxRepository.findAll().size());
  }

  @Test
  void messageReceiveTest() throws JsonProcessingException, InterruptedException {
    // send ok request, repeated request and doomed to fail request
    List<BookBuyRequest> buyRequests = List.of(
        new BookBuyRequest(UUID.randomUUID().toString(), 10L, 100L),
        new BookBuyRequest(UUID.randomUUID().toString(), 20L, 10000L)
    );
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(buyRequests.get(0)));
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(buyRequests.get(0)));
    kafkaTemplate.send("some-test-topic", objectMapper.writeValueAsString(buyRequests.get(1)));
    // wait until message is received by consumer and processed by db
    Thread.sleep(5000);

    Balance currentBalance = balanceRepository.findById(BalanceServiceImpl.mainBalanceId).get();
    assertEquals(900L, currentBalance.getValue());

    // assert appropriate results for sent out
    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(2, records.count());
    var metBuyResults = new boolean[buyRequests.size()];
    records.iterator().forEachRemaining(
        record -> {
          try {
            var value = objectMapper.readValue(record.value(), BookBuyResult.class);
            if (value.isApproved() && value.bookId() == 10L) metBuyResults[0] = true;
            if (!value.isApproved() && value.bookId() == 20L) metBuyResults[1] = true;
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
    for (int i = 0; i < metBuyResults.length; i++) {
      assertTrue(metBuyResults[i]);
    }
  }
}
