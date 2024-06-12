package com.example.travelDiary.domain.model.wallet.mapper.toentity;

import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public interface MonetaryEventEntityConverterStrategy {
    List<MonetaryEventEntity> convert(MonetaryEvent monetaryEvent);
}
