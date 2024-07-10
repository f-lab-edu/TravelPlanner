package com.roamgram.travelDiary.presentation.dto.request.wallet;

import com.roamgram.travelDiary.domain.model.wallet.Amount;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class IncomeRequest {
    private UUID id;
    private Amount amount;
    private String currency;
    private String source;
    private String description;
    private Instant timestamp;
}
