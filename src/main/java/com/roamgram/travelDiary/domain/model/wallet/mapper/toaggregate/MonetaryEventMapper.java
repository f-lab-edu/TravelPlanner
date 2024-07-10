package com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.util.List;

public interface MonetaryEventMapper {
    MonetaryEvent toAggregate(List<MonetaryEventEntity> entities);
}

