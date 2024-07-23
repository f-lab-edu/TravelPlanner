package com.roamgram.travelDiary.presentation.dto.request.wallet;

import com.roamgram.travelDiary.domain.model.wallet.Amount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CurrencyConvertRequest {
    private UUID id;
    private UUID parentScheduleId;
    private String currencyFrom;
    private String currencyTo;
    private Amount convertedAmountFrom;
    private Amount rate;
}
