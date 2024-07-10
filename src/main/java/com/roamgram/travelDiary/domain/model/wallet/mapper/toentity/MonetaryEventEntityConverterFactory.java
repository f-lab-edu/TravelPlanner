package com.roamgram.travelDiary.domain.model.wallet.mapper.toentity;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;

import java.util.Objects;

public class MonetaryEventEntityConverterFactory {
    public static MonetaryEventEntityConverterStrategy getStrategy(MonetaryEvent monetaryEvent) {
        return switch (monetaryEvent) {
            case CurrencyConversion currencyConversion -> new CurrencyConversionConverterStrategy();
            case Expenditure expenditure -> new ExpenditureConverterStrategy();
            case Income income -> new IncomeConverterStrategy();
            case null, default ->
                    throw new IllegalArgumentException("Unknown MonetaryEvent type: " + Objects.requireNonNull(monetaryEvent).getClass());
        };
    }
}

