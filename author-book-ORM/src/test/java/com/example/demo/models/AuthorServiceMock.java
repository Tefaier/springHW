package com.example.demo.models;

import com.example.demo.models.DTO.AuthorDTO;
import com.example.demo.models.service.AuthorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// looked from: https://stackoverflow.com/questions/62436256/define-common-mock-objects-for-many-test-classes
@Configuration
@ConditionalOnProperty(value = "authorService.mode", havingValue = "mock")
public class AuthorServiceMock {
  @Bean
  public AuthorService authorService() {
    AuthorService authorService = mock(AuthorService.class);
    when(authorService.getById(1L, false, false)).thenReturn(Optional.of(new AuthorDTO(1L, "first", "last", null)));
    return authorService;
  }
}
