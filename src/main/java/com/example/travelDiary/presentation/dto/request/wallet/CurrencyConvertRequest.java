package com.example.travelDiary.presentation.dto.request.wallet;

import com.example.travelDiary.domain.model.wallet.Amount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CurrencyConvertRequest {
    private UUID id;
    private String currencyFrom;
    private String currencyTo;
    private Amount convertedAmountFrom;
    private Amount convertedAmountTo;
    private BigDecimal rate;
}
