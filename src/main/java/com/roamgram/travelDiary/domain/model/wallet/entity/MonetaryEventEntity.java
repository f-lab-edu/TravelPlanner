package com.roamgram.travelDiary.domain.model.wallet.entity;

import com.roamgram.travelDiary.domain.model.wallet.Amount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonetaryEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID monetaryTransactionId;
    private BigDecimal conversionRate;
    private Amount amount;
    private Currency currency;
    private Boolean isSource; // true if it's the source entity, false if it's the destination
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String description;
    private UUID parentActivityId;
    private UUID userProfileId;
}