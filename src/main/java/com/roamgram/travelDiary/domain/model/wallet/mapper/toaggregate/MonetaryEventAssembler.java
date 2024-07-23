package com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.*;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MonetaryEventAssembler {

    public static MonetaryEvent toAggregate(MonetaryEventEntity entity, List<MonetaryEventEntity> pairedEntities) {
        MonetaryEventMapper mapper = MonetaryEventMapperFactory.getMapper(entity.getEventType());
        return mapper.toAggregate(pairedEntities);
    }

    public static List<? extends MonetaryEvent> toAggregates(List<MonetaryEventEntity> entities) {
        Map<UUID, List<MonetaryEventEntity>> groupedByTransactionId = entities.stream()
                .collect(Collectors.groupingByConcurrent(MonetaryEventEntity::getMonetaryTransactionId));

        return groupedByTransactionId.values().stream()
                .map(group -> {
                    if (group.size() == 1) {
                        return toAggregate(group.getFirst(), group);
                    } else if (group.size() == 2) {
                        return toAggregate(group.getFirst(), group);
                    } else {
                        throw new IllegalArgumentException("Unexpected number of entities for transaction ID: " + group.get(0).getMonetaryTransactionId());
                    }
                })
                .collect(Collectors.toList());
    }
}
