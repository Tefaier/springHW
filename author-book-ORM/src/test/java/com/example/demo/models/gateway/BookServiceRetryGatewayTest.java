package com.example.demo.models.gateway;

import com.example.demo.models.AuthorServiceMock;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.exceptions.BookRegistryFailException;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration;
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
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
    classes = {
        HttpBookServiceGateway.class, AuthorServiceMock.class
    },
    properties = {
        "resilience4j.retry.instances.bookRegistry.retry-exceptions[0]=com.example.demo.models.exceptions.BookRegistryFailException",
        "resilience4j.retry.instances.bookRegistry.wait-duration=100ms",
        "resilience4j.retry.instances.bookRegistry.max-attempts=5",
        "authorService.mode=mock"
    }
)
@Import(RetryAutoConfiguration.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceRetryGatewayTest {
  @Autowired
  private BookServiceGateway bookServiceGateway;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void checkRetry() {
    AtomicInteger requestTime = new AtomicInteger(0);

    when(restTemplate.exchange(
        eq("/api/book/exists?name={name}&lastName={lastName}&title={title}"),
        eq(HttpMethod.POST),
        any(),
        eq(Boolean.class),
        eq(Map.of("name", "first", "lastName", "last", "title", "book"))
    )).thenAnswer((Answer<ResponseEntity<Boolean>>) invocation -> {
      requestTime.incrementAndGet();
      return new ResponseEntity<>(true, HttpStatus.BAD_GATEWAY);
    });

    String id = UUID.randomUUID().toString();

    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book", null), id)
    );
    assertEquals(5, requestTime.get());
  }
}
