package com.example.demo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ObjectMapperTestConfig {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
