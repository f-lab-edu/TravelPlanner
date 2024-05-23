package com.example.travelDiary.application.service.wallet.aggregate;

import com.example.travelDiary.application.service.wallet.Amount;
import lombok.*;

import java.util.Currency;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expenditure implements MonetaryEvent {
    private UUID id;
    private Amount amount;
    private Currency currency;
    private String description;
}
