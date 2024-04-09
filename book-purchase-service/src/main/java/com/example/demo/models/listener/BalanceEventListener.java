package com.example.demo.models.listener;

import com.example.demo.models.DTO.BalanceUpdateEvent;
import com.example.demo.models.DTO.BookBuyResult;
import com.example.demo.models.entity.OutboxRecord;
import com.example.demo.models.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Service
public class BalanceEventListener {
  @Autowired
  private OutboxRepository outboxRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @TransactionalEventListener(phase = BEFORE_COMMIT)
  public void onBalanceUpdateEvent(BalanceUpdateEvent event) throws JsonProcessingException {
    outboxRepository.save(new OutboxRecord(
        objectMapper.writeValueAsString(
            new BookBuyResult(event.bookId(), event.isSuccessful())
        )
    ));
  }
}
