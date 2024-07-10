package com.roamgram.travelDiary.presentation.converter.request.wallet;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.presentation.dto.request.wallet.CurrencyConvertRequest;
import org.springframework.core.convert.converter.Converter;

import java.util.Currency;

public class CurrencyConversionRequestToAggregate implements Converter<CurrencyConvertRequest, CurrencyConversion> {
    @Override
    public CurrencyConversion convert(CurrencyConvertRequest source) {
        CurrencyConversion currencyConversion = new CurrencyConversion();
        if(source.getId() != null) {
            currencyConversion.setId(source.getId());
        }
        if(source.getCurrencyFrom() != null) {
            currencyConversion.setCurrencyFrom(Currency.getInstance(source.getCurrencyFrom()));
        }
        if(source.getCurrencyTo() != null) {
            currencyConversion.setCurrencyTo(Currency.getInstance(source.getCurrencyTo()));
        }
        if(source.getConvertedAmountTo() != null) {
            currencyConversion.setConvertedAmountTo(source.getConvertedAmountTo());
        }
        if(source.getConvertedAmountFrom() != null) {
            currencyConversion.setConvertedAmountFrom(source.getConvertedAmountFrom());
        }
        if(source.getRate() != null) {
            currencyConversion.setRate(source.getRate());
        }
        return currencyConversion;
    }
}
