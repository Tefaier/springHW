package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.controller.BookController;
import com.example.demo.models.exceptions.BookRegistryFailException;
import com.example.demo.models.service.AuthorService;
import com.example.demo.models.service.BookService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.Map;

@Component
public class BookServiceGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceGateway.class);

  @Autowired
  private AuthorService authorService;
  @Autowired
  private RestTemplate restTemplate;

  @RateLimiter(name = "bookRegistry", fallbackMethod = "fallbackRateLimiter")
  @CircuitBreaker(name = "bookRegistry", fallbackMethod = "fallbackCircuitBreaker")
  public boolean checkBookExists(BookDTO bookDTO) {
    try {
      if (bookDTO.authorID() == null) return false;
      var author = authorService.getById(bookDTO.id(), false, false);
      if (author.isEmpty()) return false;
      ResponseEntity<Boolean> bookResponse = restTemplate.postForEntity(
          "/api/book/exists?name={name},lastName={lastName},title={title}",
          null,
          Boolean.class,
          Map.of("name", author.get().getFirstName(), "lastName", author.get().getLastName(), "title", bookDTO.id())
      );
      LOGGER.info("Received response from author-registry-service {}", bookResponse);
      if (bookResponse.getStatusCode().is2xxSuccessful()) {
        return bookResponse.getBody();
      }
      throw new RestClientException("Unexpected status code " + bookResponse.getStatusCode());
    } catch (RestClientException e) {
      throw new BookRegistryFailException("Error while requesting author-registry-service " + e.getMessage(), e);
    }
  }

  public boolean fallbackRateLimiter(String name, RequestNotPermitted e) {
    LOGGER.warn("Error due to Rate Limiting options", e);
    throw new BookRegistryFailException(e.getMessage(), e);
  }

  public boolean fallbackCircuitBreaker(String name, RequestNotPermitted e) {
    LOGGER.warn("Error due to Circuit Breaker options", e);
    throw new BookRegistryFailException(e.getMessage(), e);
  }
}
