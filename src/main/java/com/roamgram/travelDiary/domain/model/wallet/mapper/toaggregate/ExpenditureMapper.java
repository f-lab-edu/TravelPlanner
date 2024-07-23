package com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public class ExpenditureMapper implements MonetaryEventMapper {
    @Override
    public MonetaryEvent toAggregate(List<MonetaryEventEntity> entities) {
        if (entities.size() != 1) {
            throw new IllegalArgumentException("Income events should have exactly one entity.");
        }
        MonetaryEventEntity entity = entities.getFirst();
        return Expenditure.builder()
                .id(entity.getId())
                .parentScheduleId(entity.getParentScheduleId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
