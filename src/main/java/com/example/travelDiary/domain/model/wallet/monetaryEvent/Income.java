package com.example.travelDiary.domain.model.wallet.monetaryEvent;

import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Data
public class Income implements MonetaryEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Embedded
    private Amount amount;

    private LocalDateTime timestamp;

    @Override
    public void applyTo(Wallet wallet) {
        Amount currentAmount = wallet.getAmounts().getOrDefault(currency, Amount.ZERO);
        wallet.getAmounts().put(currency, currentAmount.add(amount.getValue()));
    }
}
