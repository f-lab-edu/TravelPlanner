package com.example.travelDiary.domain.model.wallet.mapper;

import com.example.travelDiary.domain.model.wallet.aggregate.*;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.example.travelDiary.domain.model.wallet.Amount;

public class MonetaryEventMapper {

    public static MonetaryEvent toAggregate(MonetaryEventEntity entity) {
        switch (entity.getEventType()) {
            case INCOME:
                return Income.builder()
                        .id(entity.getId())
                        .amount(entity.getAmount())
                        .currency(entity.getCurrency())
                        .source(entity.getSource())
                        .description(entity.getDescription())
                        .timestamp(entity.getTimestamp())
                        .build();
            case EXPENDITURE:
                return Expenditure.builder()
                        .id(entity.getId())
                        .amount(entity.getAmount())
                        .currency(entity.getCurrency())
                        .description(entity.getDescription())
                        .build();
            case CURRENCY_CONVERSION:
                return CurrencyConversion.builder()
                        .id(entity.getId())
                        .currencyFrom(entity.getCurrency())
                        .currencyTo(entity.getCurrency())
                        .convertedAmountFrom(entity.getAmount())
                        .convertedAmountTo(new Amount(entity.getAmount().getValue().multiply(entity.getConversionRate())))
                        .rate(entity.getConversionRate())
                        .build();
            default:
                throw new IllegalArgumentException("Unknown EventType: " + entity.getEventType());
        }
    }
}
