package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.presentation.dto.request.wallet.ExpenditureRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Currency;

public class ExpenditureRequestToAggregate implements Converter<ExpenditureRequest, Expenditure> {
    @Override
    public Expenditure convert(ExpenditureRequest source) {
        Expenditure expenditure = new Expenditure();
        if (source.getId() != null) {
            expenditure.setId(source.getId());
        }
        if (source.getCurrency() != null) {
            expenditure.setCurrency(Currency.getInstance(source.getCurrency()));
        }
        if (source.getAmount() != null) {
            expenditure.setAmount(source.getAmount());
        }
        if (source.getDescription() != null) {
            expenditure.setDescription(source.getDescription());
        }

        return expenditure;
    }
}
