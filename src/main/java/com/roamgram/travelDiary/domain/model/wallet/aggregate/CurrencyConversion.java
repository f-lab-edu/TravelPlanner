package com.roamgram.travelDiary.domain.model.wallet.aggregate;

import com.roamgram.travelDiary.domain.model.wallet.Amount;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;


@JsonTypeName("currency_conversion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversion implements MonetaryEvent {
    private UUID id;
    private UUID parentScheduleId;
    private UUID transactionId;
    private Currency currencyFrom;
    private Currency currencyTo;
    private Amount convertedAmountFrom;
    private Amount convertedAmountTo;
    private Amount rate;
    private Instant timestamp;
    private final String type = "currency_conversion";
}
