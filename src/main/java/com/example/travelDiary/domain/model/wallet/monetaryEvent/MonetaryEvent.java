package com.example.travelDiary.domain.model.wallet.monetaryEvent;

import com.example.travelDiary.domain.model.wallet.Wallet;

import java.time.LocalDateTime;

public interface MonetaryEvent {
    void applyTo(Wallet wallet);
    LocalDateTime getTimestamp();
    void setTimestamp(LocalDateTime timestamp);
}
