package com.example.demo.models.gateway;

import com.example.demo.models.AuthorServiceMock;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.exceptions.BookRegistryFailException;
import com.example.demo.models.service.BookService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
    classes = {
        HttpBookServiceGateway.class, AuthorServiceMock.class
    },
    properties = {
        "resilience4j.circuitbreaker.instances.bookRegistry.slowCallRateThreshold=1",
        "resilience4j.circuitbreaker.instances.bookRegistry.slowCallDurationThreshold=1000ms",
        "resilience4j.circuitbreaker.instances.bookRegistry.slidingWindowType=COUNT_BASED",
        "resilience4j.circuitbreaker.instances.bookRegistry.slidingWindowSize=1",
        "resilience4j.circuitbreaker.instances.bookRegistry.minimumNumberOfCalls=1",
        "resilience4j.circuitbreaker.instances.bookRegistry.waitDurationInOpenState=600s",
        "authorService.mode=mock"
    }
)
@Import(CircuitBreakerAutoConfiguration.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceCBGatewayTest {
  @Autowired
  private HttpBookServiceGateway bookServiceGateway;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void shouldRejectRequestAfterFirstServerSlowResponse() {
    when(restTemplate.exchange(
        eq("/api/book/exists?name={name}&lastName={lastName}&title={title}"),
        eq(HttpMethod.POST),
        any(),
        eq(Boolean.class),
        eq(Map.of("name", "first", "lastName", "last", "title", "book"))
    )).thenAnswer((Answer<ResponseEntity<Boolean>>) invocation -> {
      Thread.sleep(2000);
      return new ResponseEntity<>(true, HttpStatus.OK);
    });

    assertDoesNotThrow(
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book", null, null), UUID.randomUUID().toString())
    );
    // slow response filled CB limit
    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book", null, null), UUID.randomUUID().toString())
    );
  }

  @Test
  void shouldRejectRequestAfterFirstServerFailResponse() {
    String debugString = "ABSDEFGHI...";

    when(restTemplate.exchange(
        eq("/api/book/exists?name={name}&lastName={lastName}&title={title}"),
        eq(HttpMethod.POST),
        any(),
        eq(Boolean.class),
        eq(Map.of("name", "first", "lastName", "last", "title", "book"))
    )).thenThrow(new RestClientException(debugString));

    when(restTemplate.exchange(
        eq("/api/book/exists?name={name}&lastName={lastName}&title={title}"),
        eq(HttpMethod.POST),
        any(),
        eq(Boolean.class),
        eq(Map.of("name", "first", "lastName", "last", "title", "book2"))
    )).thenAnswer((Answer<ResponseEntity<Boolean>>) invocation -> new ResponseEntity<>(true, HttpStatus.OK));

    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book", null, null), UUID.randomUUID().toString())
    );
    // CB rejects due to failed request before
    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book2", null, null), UUID.randomUUID().toString())
    );
  }
}
