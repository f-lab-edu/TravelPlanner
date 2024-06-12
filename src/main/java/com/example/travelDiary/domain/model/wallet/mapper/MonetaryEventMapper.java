package com.example.travelDiary.domain.model.wallet.mapper;

import com.example.travelDiary.domain.model.wallet.aggregate.*;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonetaryEventMapper {

    public static MonetaryEvent toAggregate(MonetaryEventEntity entity, MonetaryEventEntity pairedEntity) {
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
                if (pairedEntity == null) {
                    throw new IllegalArgumentException("Paired entity for currency conversion not found.");
                }
                if (entity.getIsSource()) {
                    return CurrencyConversion.builder()
                            .id(entity.getId())
                            .currencyFrom(entity.getCurrency())
                            .currencyTo(pairedEntity.getCurrency())
                            .convertedAmountFrom(entity.getAmount())
                            .convertedAmountTo(pairedEntity.getAmount())
                            .rate(entity.getConversionRate())
                            .build();
                } else {
                    return CurrencyConversion.builder()
                            .id(entity.getId())
                            .currencyFrom(pairedEntity.getCurrency())
                            .currencyTo(entity.getCurrency())
                            .convertedAmountFrom(pairedEntity.getAmount())
                            .convertedAmountTo(entity.getAmount())
                            .rate(entity.getConversionRate())
                            .build();
                }
            default:
                throw new IllegalArgumentException("Unknown EventType: " + entity.getEventType());
        }
    }

    public static List<MonetaryEvent> toAggregates(List<MonetaryEventEntity> entities) {
        Map<String, List<MonetaryEventEntity>> groupedByTransactionId = entities.stream()
                .collect(Collectors.groupingByConcurrent(MonetaryEventEntity::getTransactionId));

        return groupedByTransactionId.values().stream()
                .flatMap(group -> {
                    if (group.size() == 1) {
                        return group.stream().map(entity ->toAggregate(entity, null));
                    } else if (group.size() == 2) {
                        MonetaryEventEntity entity1 = group.get(0);
                        MonetaryEventEntity entity2 = group.get(1);
                        return Stream.of(toAggregate(entity1, entity2));
                    } else {
                        throw new IllegalArgumentException("Unexpected number of entities for transaction ID: " + group.getFirst().getTransactionId());
                    }
                })
                .collect(Collectors.toList());
    }
}
