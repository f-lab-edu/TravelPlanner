package com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public class CurrencyConversionMapper implements MonetaryEventMapper {
    @Override
    public MonetaryEvent toAggregate(List<MonetaryEventEntity> entities) {
        if (entities.size() != 2) {
            throw new IllegalArgumentException("Currency conversion events should have exactly two entities.");
        }
        MonetaryEventEntity entity1 = entities.get(0);
        MonetaryEventEntity entity2 = entities.get(1);

        if (!entity1.getParentScheduleId().equals(entity2.getParentScheduleId())) {
            throw new IllegalArgumentException("the 2 currencyConversion events have different parentActivities id. corrupted data");
        }

        if (entity1.getIsSource()) {
            return CurrencyConversion.builder()
                    .id(entity1.getId())
                    .parentScheduleId(entity1.getParentScheduleId())
                    .transactionId(entity1.getMonetaryTransactionId())
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
                    .parentScheduleId(entity2.getParentScheduleId())
                    .transactionId(entity2.getMonetaryTransactionId())
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
