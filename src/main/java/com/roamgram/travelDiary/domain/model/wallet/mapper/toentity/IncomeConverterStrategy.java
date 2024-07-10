package com.roamgram.travelDiary.domain.model.wallet.mapper.toentity;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.EventType;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class IncomeConverterStrategy implements MonetaryEventEntityConverterStrategy {

    @Override
    public List<MonetaryEventEntity> convert(MonetaryEvent monetaryEvent) {
        Income income = (Income) monetaryEvent;
        return List.of(
                MonetaryEventEntity
                        .builder()
                        .transactionId(UUID.randomUUID().toString())
                        .source("source")
                        .amount(income.getAmount())
                        .currency(income.getCurrency())
                        .description(income.getDescription())
                        .timestamp(Instant.now())
                        .eventType(EventType.INCOME)
                        .build()
        );
    }
}
