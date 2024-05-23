//package com.example.travelDiary.repository.persistence.wallet.monetaryEvent;
//
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.CurrencyConversion;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.Expenditure;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.Income;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.MonetaryEvent;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//public interface MonetaryEventRepository extends JpaRepository<MonetaryEvent, UUID> {
//
//    List<MonetaryEvent> findByTimestampBeforeOrderByTimestamp(LocalDateTime timestamp);
//
//    @Query("SELECT e FROM Expenditure e WHERE e.timestamp BETWEEN :start AND :end")
//    List<Expenditure> findExpendituresBetween(LocalDateTime start, LocalDateTime end);
//
//    @Query("SELECT i FROM Income i WHERE i.timestamp BETWEEN :start AND :end")
//    List<Income> findIncomesBetween(LocalDateTime start, LocalDateTime end);
//
//    @Query("SELECT c FROM CurrencyConversion c WHERE c.timestamp BETWEEN :start AND :end")
//    List<CurrencyConversion> findConversionsBetween(LocalDateTime start, LocalDateTime end);
//
//    @Query("SELECT e FROM Expenditure e")
//    List<Expenditure> findAllExpenditures();
//
//    @Query("SELECT i FROM Income i")
//    List<Income> findAllIncome();
//
//    @Query("SELECT c FROM CurrencyConversion c")
//    List<CurrencyConversion> findAllCurrencyConversions();
//}
