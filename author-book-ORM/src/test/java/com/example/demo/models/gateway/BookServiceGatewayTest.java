package com.example.demo.models.gateway;

import com.example.demo.models.DTO.BookDTO;
import com.example.demo.models.config.RestTemplateConfiguration;
import com.example.demo.models.exceptions.BookRegistryFailException;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.Parameter.param;

@Testcontainers
@SpringBootTest(classes = {BookServiceGateway.class, RestTemplateConfiguration.class})
class BookServiceGatewayTest {
  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry.service.base.url", mockServer::getEndpoint);
  }

  @Autowired
  private BookServiceGateway bookServiceGateway;

  @Test
  void chekc() {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
        .when(request()
            .withPath("/api/book/exists")
            .withQueryStringParameter(param("name", "", "lastName", "", "title", "")))
        .respond(req -> {
          Thread.sleep(3000);
          return HttpResponse.response(
                  """
                      { true }
                      """
          ).withHeader("Content-Type", "application/json");
        });

    assertThrows(
        BookRegistryFailException.class,
        () -> bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "title", null))
    );

    Boolean exists = bookServiceGateway.checkBookExists(new BookDTO(null, 1L, "title", null));

  }
}