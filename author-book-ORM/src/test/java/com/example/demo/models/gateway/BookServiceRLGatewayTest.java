package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.exceptions.BookRegistryFailException;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
    classes = {
        BookServiceGateway.class
    },
    properties = {
        "resilience4j.ratelimiter.instances.bookRegistry.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.bookRegistry.limitRefreshPeriod=1h",
        "resilience4j.ratelimiter.instances.bookRegistry.timeoutDuration=0"
    }
)
@Import(RateLimiterAutoConfiguration.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceRLGatewayTest {
  @Autowired
  private BookServiceGateway bookServiceGateway;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void rateLimiterTest() {
    when(restTemplate.postForEntity(
        eq("/api/book/exists?name={name},lastName={lastName},title={title}"),
        null,
        eq(Boolean.class),
        eq(Map.of("name", "МГТУ", "lastName", "", "title", ""))
    )).thenAnswer((Answer<ResponseEntity<Boolean>>) invocation -> {
      Thread.sleep(2000);
      return new ResponseEntity<>(true, HttpStatus.OK);
    });

    assertDoesNotThrow(
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "", null))
    );

    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "", null))
    );
  }
}
