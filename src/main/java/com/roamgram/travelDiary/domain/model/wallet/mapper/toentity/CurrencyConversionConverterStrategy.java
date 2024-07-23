package com.roamgram.travelDiary.domain.model.wallet.mapper.toentity;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.EventType;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CurrencyConversionConverterStrategy implements MonetaryEventEntityConverterStrategy {

    @Override
    public List<MonetaryEventEntity> convert(MonetaryEvent monetaryEvent) {
        CurrencyConversion currencyConversion = (CurrencyConversion) monetaryEvent;
        UUID transactionId = UUID.randomUUID();
        Instant now = Instant.now();
        MonetaryEventEntity from = MonetaryEventEntity
                .builder()
                .monetaryTransactionId(transactionId)
                .amount(currencyConversion.getConvertedAmountFrom().negate())
                .parentScheduleId(currencyConversion.getParentScheduleId())
                .currency(currencyConversion.getCurrencyFrom())
                .isSource(true)
                .conversionRate(currencyConversion.getRate())
                .timestamp(now)
                .eventType(EventType.CURRENCY_CONVERSION)
                .build();
        MonetaryEventEntity to = MonetaryEventEntity
                .builder()
                .monetaryTransactionId(transactionId)
                .amount(currencyConversion.getConvertedAmountTo())
                .parentScheduleId(currencyConversion.getParentScheduleId())
                .isSource(false)
                .conversionRate(currencyConversion.getRate())
                .currency(currencyConversion.getCurrencyTo())
                .timestamp(now)
                .eventType(EventType.CURRENCY_CONVERSION)
                .build();
        return List.of(from, to);
    }
}

