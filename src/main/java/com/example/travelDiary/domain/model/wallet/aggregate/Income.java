package com.example.travelDiary.domain.model.wallet.aggregate;

import com.example.travelDiary.domain.IdentifiableResource;
import com.example.travelDiary.domain.model.wallet.Amount;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@JsonTypeName("income")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Income implements MonetaryEvent {
    private UUID id;
    private Amount amount;
    private Currency currency;
    private String source;
    private String description;
    private Instant timestamp;

}
