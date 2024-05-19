package com.example.travelDiary.domain.model.wallet.expenditure;

import com.example.travelDiary.domain.model.tags.Category;
import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.currency.Currency;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @OneToOne
    public Currency currency;

    public Amount amount;

    @ManyToOne
    public Category category;

}
