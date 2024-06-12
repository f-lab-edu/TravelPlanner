package com.example.travelDiary.repository.persistence.wallet;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public interface MonetaryEventEntityRepository extends JpaRepository<MonetaryEventEntity, UUID> {
    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'INCOME'")
    Page<Income> findAllIncomes(Pageable pageable);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'EXPENDITURE'")
    Page<Expenditure> findAllExpenditure(Pageable page);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'CURRENCYCONVERSION' GROUP BY e.transactionId")
    Page<CurrencyConversion> findAllCurrencyConversion(Pageable page);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'INCOME'")
    Page<Income> findAllIncomesBetweenTimeStamp(Pageable pageable, Instant from, Instant to);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'EXPENDITURE'")
    Page<Expenditure> findAllExpenditureBetweenTimeStamp(Pageable page, Instant from, Instant to);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'CURRENCYCONVERSION' GROUP BY e.transactionId")
    Page<CurrencyConversion> findAllCurrencyConversionBetweenTimeStamp(Pageable page, Instant from, Instant to);

    Page<MonetaryEvent> findAllMonetaryEventBetweenTimeStamp(Pageable page, Instant from, Instant to);

}
