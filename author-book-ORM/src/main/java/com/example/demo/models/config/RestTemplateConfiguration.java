package com.example.demo.models.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfiguration {
  @Bean
  public RestTemplate restTemplate(
      @Value("{author-registry.service.base.url}") String baseUrl,
      @Value("{author-registry.service.timeout.seconds}") Duration timeout
  ) {
    return new RestTemplateBuilder()
        .setConnectTimeout(timeout)
        .setReadTimeout(timeout)
        .rootUri(baseUrl)
        .build();
  }
}
