package com.example.travelDiary.presentation.converter.request.wallet;

import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.presentation.dto.request.wallet.IncomeRequest;
import org.springframework.core.convert.converter.Converter;

import java.util.Currency;


public class IncomeRequestToAggregate implements Converter<IncomeRequest, Income> {
    @Override
    public Income convert(IncomeRequest source) {
        Income income = new Income();
        if (source.getId() != null) {
            income.setId(source.getId());
        }
        if (source.getDescription() != null) {
            income.setDescription(source.getDescription());
        }

        if (source.getAmount() != null) {
            income.setAmount(source.getAmount());
        }

        if (source.getCurrency() != null) {
            income.setCurrency(Currency.getInstance(source.getCurrency()));
        }

        if (source.getSource() != null) {
            income.setSource(source.getSource());
        }

        if (source.getTimestamp() != null) {
            income.setTimestamp(source.getTimestamp());
        }
        return income;
    }
}
