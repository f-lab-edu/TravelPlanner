package com.example.travelDiary.domain.model.wallet.mapper.toaggregate;
import com.example.travelDiary.domain.model.wallet.entity.EventType;


public class MonetaryEventMapperFactory {
    public static MonetaryEventMapper getMapper(EventType eventType) {
        return switch (eventType) {
            case INCOME -> new IncomeMapper();
            case EXPENDITURE -> new ExpenditureMapper();
            case CURRENCY_CONVERSION -> new CurrencyConversionMapper();
            default -> throw new IllegalArgumentException("Unknown EventType: " + eventType);
        };
    }
}

