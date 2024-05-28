package com.example.travelDiary.domain.model.wallet.currency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String currency;
}
