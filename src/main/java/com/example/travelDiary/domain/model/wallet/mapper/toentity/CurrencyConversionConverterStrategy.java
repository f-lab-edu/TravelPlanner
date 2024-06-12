package com.example.travelDiary.domain.model.wallet.mapper.toentity;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.EventType;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CurrencyConversionConverterStrategy implements MonetaryEventEntityConverterStrategy {

    @Override
    public List<MonetaryEventEntity> convert(MonetaryEvent monetaryEvent) {
        CurrencyConversion currencyConversion = (CurrencyConversion) monetaryEvent;
        UUID transactionId = UUID.randomUUID();
        Instant now = Instant.now();
        MonetaryEventEntity from = MonetaryEventEntity
                .builder()
                .transactionId(transactionId.toString())
                .source("source")
                .amount(currencyConversion.getConvertedAmountFrom().negate())
                .currency(currencyConversion.getCurrencyFrom())
                .timestamp(now)
                .eventType(EventType.CURRENCY_CONVERSION)
                .build();
        MonetaryEventEntity to = MonetaryEventEntity
                .builder()
                .transactionId(transactionId.toString())
                .source("source")
                .amount(currencyConversion.getConvertedAmountTo())
                .currency(currencyConversion.getCurrencyTo())
                .timestamp(now)
                .eventType(EventType.CURRENCY_CONVERSION)
                .build();
        return List.of(from, to);
    }
}

