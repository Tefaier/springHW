package com.example.demo.models.service;

import com.example.demo.models.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceService{
  public static final Long mainBalanceId = 1L;

  @Autowired
  private BalanceRepository balanceRepository;

  @Override
  public void tryBuyBook(Long bookId, Long price) {
    var balance = balanceRepository.findByIdForUpdate(mainBalanceId).orElseThrow();
    balance.updateValue(bookId, price * -1);
    balanceRepository.save(balance);
  }
}
