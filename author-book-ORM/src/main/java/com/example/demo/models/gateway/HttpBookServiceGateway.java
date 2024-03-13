package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.DTO.BooleanDTO;
import com.example.demo.models.exceptions.BookRegistryFailException;
import com.example.demo.models.service.AuthorService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@ConditionalOnProperty(value = "author-registry.mode", havingValue = "http")
public class HttpBookServiceGateway implements BookServiceGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpBookServiceGateway.class);

  @Autowired
  private AuthorService authorService;
  @Autowired
  private RestTemplate restTemplate;

  @RateLimiter(name = "bookRegistry")//, fallbackMethod = "fallbackRateLimiter")
  @CircuitBreaker(name = "bookRegistry")//, fallbackMethod = "fallbackCircuitBreaker")
  @Retry(name = "bookRegistry")
  @Override
  public BooleanDTO checkBookExists(BookDTO bookDTO, String requestId) {
    try {
      if (bookDTO.authorID() == null) return new BooleanDTO(false);
      var author = authorService.getById(bookDTO.authorID(), false, false);
      if (author.isEmpty()) return new BooleanDTO(false);
      HttpHeaders headers = new HttpHeaders();
      // фиксируем уникальный request-id
      headers.add("X-REQUEST-ID", requestId);
      ResponseEntity<BooleanDTO> bookResponse = restTemplate.exchange(
          "/api/book/exists?name={name}&lastName={lastName}&title={title}",
          HttpMethod.POST,
          new HttpEntity<>(headers),
          BooleanDTO.class,
          Map.of("name", author.get().getFirstName(), "lastName", author.get().getLastName(), "title", bookDTO.title())
      );
      LOGGER.info("Received response from author-registry-service {}", bookResponse);
      if (bookResponse.getStatusCode().is2xxSuccessful()) {
        return new BooleanDTO(bookResponse.getBody().value());
      }
      throw new RestClientException("Unexpected status code " + bookResponse.getStatusCode());
    } catch (RestClientException e) {
      throw new BookRegistryFailException("Error while requesting author-registry-service " + e.getMessage(), e);
    }
  }

  private BooleanDTO fallbackRateLimiter(BookDTO bookDTO, String requestId, RequestNotPermitted e) {
    LOGGER.warn("Error due to Rate Limiting options", e);
    throw new BookRegistryFailException(e.getMessage(), e);
  }

  private BooleanDTO fallbackCircuitBreaker(BookDTO bookDTO, String requestId, RequestNotPermitted e) {
    LOGGER.warn("Error due to Circuit Breaker options", e);
    throw new BookRegistryFailException(e.getMessage(), e);
  }
}
