package com.roamgram.travelDiary.repository.persistence.wallet;

import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MonetaryEventEntityRepository extends JpaRepository<MonetaryEventEntity, UUID> {

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'EXPENDITURE'")
    Page<Expenditure> findAllExpenditure(Pageable page);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'CURRENCYCONVERSION' ORDER BY CAST(e.monetaryTransactionId AS string)")
    Page<CurrencyConversion> findAllCurrencyConversion(Pageable page);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'INCOME' AND e.timestamp >= :from AND e.timestamp <= :to")
    Page<Income> findAllIncomesBetweenTimeStamp(Pageable pageable, Instant from, Instant to);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'EXPENDITURE' AND e.timestamp >= :from AND e.timestamp <= :to")
    Page<Expenditure> findAllExpenditureBetweenTimeStamp(Pageable page, Instant from, Instant to);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'CURRENCYCONVERSION'AND e.timestamp >= :from AND e.timestamp <= :to ORDER BY CAST(e.monetaryTransactionId AS string)")
    Page<CurrencyConversion> findAllCurrencyConversionBetweenTimeStamp(Pageable page, Instant from, Instant to);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.timestamp >= :from AND e.timestamp <= :to ORDER BY CAST(e.monetaryTransactionId AS string)")
    Page<MonetaryEvent> findAllMonetaryEventBetweenTimeStamp(Pageable page, Instant from, Instant to);

    MonetaryEventEntity findByMonetaryTransactionId(UUID monetaryTransactionId);

    List<MonetaryEventEntity> findAllByMonetaryTransactionId(UUID monetaryTransactionId);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.activities a " +
            "JOIN a.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'INCOME'" )
    Page<Income> findAllIncomeFromTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.activities a " +
            "JOIN a.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'EXPENDITURE'" )
    Page<Expenditure> findAllExpenditureFromTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.activities a " +
            "JOIN a.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'CURRENCYCONVERSION'" +
            "ORDER BY CAST(mee.monetaryTransactionId AS string)" )
    Page<CurrencyConversion> findAllCurrencyConversionFromTravelPlan(UUID travelPlanId, Pageable page);


    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.activities a " +
            "JOIN a.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.timestamp >= :from " +
            "AND mee.timestamp <= :to " +
            "ORDER BY CAST(mee.monetaryTransactionId AS string)" )
    Page<MonetaryEvent> findAllMonetaryEventBetweenTimeStampInTravelPlan(UUID travelPlanId, Instant from, Instant to, Pageable page);
}
