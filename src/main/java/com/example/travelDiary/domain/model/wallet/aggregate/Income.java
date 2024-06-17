package com.example.travelDiary.domain.model.wallet.aggregate;

import com.example.travelDiary.domain.IdentifiableResource;
import com.example.travelDiary.domain.model.wallet.Amount;
import lombok.*;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Income implements MonetaryEvent, IdentifiableResource {
    private UUID id;
    private Amount amount;
    private Currency currency;
    private String source;
    private String description;
    private Instant timestamp;

}
