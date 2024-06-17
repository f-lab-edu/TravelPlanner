package com.example.travelDiary.domain.model.wallet.aggregate;

import com.example.travelDiary.domain.IdentifiableResource;
import com.example.travelDiary.domain.model.wallet.Amount;
import io.jsonwebtoken.Identifiable;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversion implements MonetaryEvent {
    private UUID id;
    private Currency currencyFrom;
    private Currency currencyTo;
    private Amount convertedAmountFrom;
    private Amount convertedAmountTo;
    private BigDecimal rate;
    private Instant timestamp;
}
