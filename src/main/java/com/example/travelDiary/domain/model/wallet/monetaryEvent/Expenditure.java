package com.example.travelDiary.domain.model.wallet.monetaryEvent;

import com.example.travelDiary.domain.model.tags.Category;
import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Data
public class Expenditure implements MonetaryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public Currency currency;

    public Amount amount;

    @ManyToOne
    public Category category;

    private LocalDateTime timestamp;

    @Override
    public void applyTo(Wallet wallet) {
        Amount currentAmount = wallet.getAmounts().getOrDefault(currency, Amount.ZERO);
        wallet.getAmounts().put(currency, currentAmount.subtract(amount.getValue()));
    }

}
