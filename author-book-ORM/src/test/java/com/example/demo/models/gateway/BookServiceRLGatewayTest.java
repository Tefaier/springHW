package com.example.demo.models.gateway;

import com.example.demo.models.AuthorServiceMock;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.exceptions.BookRegistryFailException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

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
        "resilience4j.ratelimiter.instances.bookRegistry.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.bookRegistry.limitRefreshPeriod=1h",
        "resilience4j.ratelimiter.instances.bookRegistry.timeoutDuration=10ms",
        "authorService.mode=mock"
    }
)
@Import(RateLimiterAutoConfiguration.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceRLGatewayTest {
  @Autowired
  private HttpBookServiceGateway bookServiceGateway;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void rateLimiterTest() {
    when(restTemplate.exchange(
        eq("/api/book/exists?name={name}&lastName={lastName}&title={title}"),
        eq(HttpMethod.POST),
        any(),
        eq(Boolean.class),
        eq(Map.of("name", "first", "lastName", "last", "title", "book"))
    )).thenAnswer((Answer<ResponseEntity<Boolean>>) invocation -> {
      return new ResponseEntity<>(true, HttpStatus.OK);
    });

    assertDoesNotThrow(
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "book", null, null), UUID.randomUUID().toString())
    );
    // limit was filled
    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(2L, 1L, "book", null, null), UUID.randomUUID().toString())
    );
  }
}
