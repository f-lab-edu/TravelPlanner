package com.example.travelDiary.domain.model.wallet;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Currency;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "wallet_amounts", joinColumns = @JoinColumn(name = "wallet_id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "amount")
    Map<Currency, Amount> amounts;

}
