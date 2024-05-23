package com.example.travelDiary.presentation.dto.request.wallet;

import com.example.travelDiary.application.service.wallet.Amount;
import lombok.Data;

import java.util.UUID;

@Data
public class ExpenditureRequest {
    private UUID id;
    private Amount amount;
    private String currency;
    private String description;
}
