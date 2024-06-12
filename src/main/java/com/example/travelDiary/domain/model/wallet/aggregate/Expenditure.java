package com.example.travelDiary.domain.model.wallet.aggregate;

import com.example.travelDiary.domain.model.wallet.Amount;
import lombok.*;

import java.time.Instant;
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
    private Instant timestamp;

}
