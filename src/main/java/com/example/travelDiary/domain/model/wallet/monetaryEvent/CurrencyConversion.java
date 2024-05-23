package com.example.travelDiary.domain.model.wallet.monetaryEvent;

import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@DiscriminatorValue("conversion")
@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyConversion extends MonetaryEvent {

    private Amount amountFrom;
    private String currencyFrom;

    private Amount amountTo;
    private String currencyTo;
    private Amount rate;

}
