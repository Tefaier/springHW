package com.example.demo.models.entity;

import com.example.demo.models.DTO.BalanceUpdateEvent;
import com.example.demo.models.service.BalanceService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.AbstractAggregateRoot;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "balance")
public class Balance extends AbstractAggregateRoot<Balance> {
  @Id
  private Long id;

  @PositiveOrZero
  @NotNull
  private Long value;

  protected Balance() {
  }

  public Balance(Long id, Long value) {
    this.id = id;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public Long getValue() {
    return value;
  }

  @Transactional
  public void updateValue(Long forBookId, Long change) {
    if ((value + change < 0)) {
      registerEvent(new BalanceUpdateEvent(forBookId, false));
    } else {
      value += change;
      registerEvent(new BalanceUpdateEvent(forBookId, true));
    }
  }
}
