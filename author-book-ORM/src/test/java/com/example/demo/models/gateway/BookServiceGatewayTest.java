package com.example.demo.models.gateway;

import com.example.demo.models.AuthorServiceMock;
import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.config.RestTemplateConfiguration;
import com.example.demo.models.exceptions.BookRegistryFailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.Parameter.param;

@Testcontainers
@SpringBootTest(
    classes = {HttpBookServiceGateway.class, RestTemplateConfiguration.class, AuthorServiceMock.class},
    properties = {"authorService.mode=mock"})
class BookServiceGatewayTest {
  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry.service.base.url", mockServer::getEndpoint);
  }

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private BookServiceGateway bookServiceGateway;

  @Test
  void checkOfTimeout() {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
        .when(request()
            .withPath("/api/book/exists")
            .withQueryStringParameters(
                List.of(
                    new Parameter("name", "first"),
                    new Parameter("lastName", "last"),
                    new Parameter("title", "1"))))
        .respond(req -> {
          Thread.sleep(3000);
          return HttpResponse.response("true").withHeader("Content-Type", "application/json");
        });
    client
        .when(request()
            .withPath("/api/book/exists")
            .withQueryStringParameters(
                List.of(
                    new Parameter("name", "first"),
                    new Parameter("lastName", "last"),
                    new Parameter("title", "2"))))
        .respond(req -> HttpResponse.response("true").withHeader("Content-Type", "application/json"));

    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "1", null), UUID.randomUUID().toString())
    );

    Boolean exists = bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "2", null), UUID.randomUUID().toString());
    assertTrue(exists);
  }
}