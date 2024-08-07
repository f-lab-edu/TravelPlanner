package com.roamgram.travelDiary.presentation.dto.request.wallet;

import com.roamgram.travelDiary.domain.model.wallet.Amount;
import lombok.Data;

import java.util.UUID;

@Data
public class ExpenditureRequest {
    private UUID id;
    private UUID parentScheduleId;
    private Amount amount;
    private String currency;
    private String description;
}
