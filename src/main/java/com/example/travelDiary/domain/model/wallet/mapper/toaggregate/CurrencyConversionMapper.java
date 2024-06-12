package com.example.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public class CurrencyConversionMapper implements MonetaryEventMapper {
    @Override
    public MonetaryEvent toAggregate(List<MonetaryEventEntity> entities) {
        if (entities.size() != 2) {
            throw new IllegalArgumentException("Currency conversion events should have exactly two entities.");
        }
        MonetaryEventEntity entity1 = entities.get(0);
        MonetaryEventEntity entity2 = entities.get(1);

        if (entity1.getIsSource()) {
            return CurrencyConversion.builder()
                    .id(entity1.getId())
                    .currencyFrom(entity1.getCurrency())
                    .currencyTo(entity2.getCurrency())
                    .convertedAmountFrom(entity1.getAmount())
                    .convertedAmountTo(entity2.getAmount())
                    .rate(entity1.getConversionRate())
                    .timestamp(entity1.getTimestamp())
                    .build();
        } else {
            return CurrencyConversion.builder()
                    .id(entity1.getId())
                    .currencyFrom(entity2.getCurrency())
                    .currencyTo(entity1.getCurrency())
                    .convertedAmountFrom(entity2.getAmount())
                    .convertedAmountTo(entity1.getAmount())
                    .rate(entity1.getConversionRate())
                    .timestamp(entity2.getTimestamp())
                    .build();
        }
    }
}
