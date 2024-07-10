package com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public class IncomeMapper implements MonetaryEventMapper {

    @Override
    public MonetaryEvent toAggregate(List<MonetaryEventEntity> entities) {
        if (entities.size() != 1) {
            throw new IllegalArgumentException("Income events should have exactly one entity.");
        }
        MonetaryEventEntity entity = entities.getFirst();
        return Income.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .source(entity.getSource())
                .description(entity.getDescription())
                .timestamp(entity.getTimestamp())
                .build();
    }
}

