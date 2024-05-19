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
public class CurrencyConversion implements MonetaryEvent{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    private Currency toCurrency;

    @Embedded
    private Amount fromAmount;

    @Embedded
    private Amount toAmount;

    private LocalDateTime timestamp;

    @Override
    public void applyTo(Wallet wallet) {
        Amount currentFromAmount = wallet.getAmounts().getOrDefault(fromCurrency, Amount.ZERO);
        Amount currentToAmount = wallet.getAmounts().getOrDefault(toCurrency, Amount.ZERO);
        wallet.getAmounts().put(fromCurrency, currentFromAmount.subtract(fromAmount.getValue()));
        wallet.getAmounts().put(toCurrency, currentToAmount.add(toAmount.getValue()));
    }
}
