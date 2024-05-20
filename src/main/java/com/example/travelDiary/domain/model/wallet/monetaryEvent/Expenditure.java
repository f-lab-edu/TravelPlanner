package com.example.travelDiary.domain.model.wallet.monetaryEvent;

import com.example.travelDiary.domain.model.tags.Category;
import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("expenditure")
@Data
public class Expenditure extends MonetaryEvent {

    private Amount amount;
    private Currency currency;
    private String description;

}
