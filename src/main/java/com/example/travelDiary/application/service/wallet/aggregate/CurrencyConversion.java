package com.example.travelDiary.application.service.wallet.aggregate;

import com.example.travelDiary.application.service.wallet.Amount;
import lombok.*;

import java.math.BigDecimal;
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
}
