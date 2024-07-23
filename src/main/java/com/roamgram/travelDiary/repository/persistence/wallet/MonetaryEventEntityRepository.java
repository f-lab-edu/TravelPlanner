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
    Page<MonetaryEventEntity> findAllExpenditure(Pageable page);

    @Query("SELECT e FROM MonetaryEventEntity e WHERE e.eventType = 'CURRENCY_CONVERSION' ORDER BY CAST(e.monetaryTransactionId AS string)")
    Page<MonetaryEventEntity> findAllCurrencyConversion(Pageable page);

    MonetaryEventEntity findByMonetaryTransactionId(UUID monetaryTransactionId);

    List<MonetaryEventEntity> findAllByMonetaryTransactionId(UUID monetaryTransactionId);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'INCOME'" +
            "ORDER BY mee.timestamp" )
    Page<MonetaryEventEntity> findAllIncomeFromTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'EXPENDITURE'" +
            "ORDER BY mee.timestamp" )
    Page<MonetaryEventEntity> findAllExpenditureFromTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.eventType = 'CURRENCY_CONVERSION'" +
            "ORDER BY mee.monetaryTransactionId, mee.timestamp ASC"  )
    Page<MonetaryEventEntity> findAllCurrencyConversionFromTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "AND mee.timestamp >= :from " +
            "AND mee.timestamp <= :to " +
            "ORDER BY mee.monetaryTransactionId, mee.timestamp ASC"  )
    Page<MonetaryEventEntity> findAllMonetaryEventBetweenTimeStampInTravelPlan(UUID travelPlanId, Instant from, Instant to, Pageable page);

    @Query("SELECT mee FROM TravelPlan tp " +
            "JOIN tp.scheduleList s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE tp.id = :travelPlanId " +
            "ORDER BY mee.monetaryTransactionId , mee.timestamp ASC" )
    Page<MonetaryEventEntity> findAllMonetaryEventInTravelPlan(UUID travelPlanId, Pageable page);

    @Query("SELECT mee FROM Schedule s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE s.id = :scheduleId " +
            "AND mee.eventType = 'INCOME'" +
            "ORDER BY mee.timestamp" )
    Page<MonetaryEventEntity> findAllIncomeFromSchedule(UUID scheduleId, Pageable page);

    @Query("SELECT mee FROM Schedule s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE s.id = :scheduleId " +
            "AND mee.eventType = 'EXPENDITURE'" +
            "ORDER BY mee.timestamp" )
    Page<MonetaryEventEntity> findAllExpenditureFromSchedule(UUID scheduleId, Pageable page);
    @Query("SELECT mee FROM Schedule s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE s.id = :scheduleId " +
            "AND mee.eventType = 'CURRENCY_CONVERSION'" +
            "ORDER BY mee.monetaryTransactionId, mee.timestamp ASC" )
    Page<MonetaryEventEntity> findAllCurrencyConversionFromSchedule(UUID scheduleId, Pageable page);
    @Query("SELECT mee FROM Schedule s " +
            "JOIN s.monetaryEvents mee " +
            "WHERE s.id = :scheduleId " +
            "ORDER BY mee.monetaryTransactionId, mee.timestamp ASC")
    Page<MonetaryEventEntity> findAllMonetaryEventFromSchedule(UUID scheduleId, Pageable page);
}

