package com.roamgram.travelDiary.presentation.controller.monetaryEvent;

import com.roamgram.travelDiary.application.service.wallet.MonetaryDomainQueryService;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.roamgram.travelDiary.application.service.wallet.MonetaryDomainMutationService;
import com.roamgram.travelDiary.presentation.dto.request.wallet.CurrencyConvertRequest;
import com.roamgram.travelDiary.presentation.dto.request.wallet.ExpenditureRequest;
import com.roamgram.travelDiary.presentation.dto.request.wallet.IncomeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate.MonetaryEventAssembler.toAggregates;

@RestController
@RequestMapping("/api/monetary")
public class MonetaryEventController {
    private final MonetaryDomainMutationService monetaryDomainMutationService;
    private final MonetaryDomainQueryService monetaryDomainQueryService;
    private final ConversionService conversionService;

    @Autowired
    public MonetaryEventController(MonetaryDomainMutationService monetaryDomainMutationService, MonetaryDomainQueryService monetaryDomainQueryService, ConversionService conversionService) {
        this.monetaryDomainMutationService = monetaryDomainMutationService;
        this.monetaryDomainQueryService = monetaryDomainQueryService;
        this.conversionService = conversionService;
    }

    @DeleteMapping("/delete-event")
    public ResponseEntity<String> deleteMonetaryEvent(UUID transactionId) {
        monetaryDomainMutationService.delete(transactionId);
        return ResponseEntity.ok("deleted" + transactionId.toString());
    }

    @PatchMapping("/update-event")
    public ResponseEntity<List<MonetaryEvent>> updateEvent(UUID transactionId, MonetaryEvent updateFields) {
        List<MonetaryEventEntity> event = monetaryDomainMutationService.update(transactionId, updateFields);
        return ResponseEntity.ok((List<MonetaryEvent>)toAggregates(event));
    }

    @PutMapping("/new-income")
    public ResponseEntity<List<MonetaryEventEntity>> createIncome(@RequestBody IncomeRequest income){
        return ResponseEntity.ok(monetaryDomainMutationService.save(income.getParentScheduleId(), conversionService.convert(income,Income.class)));
    }

    @GetMapping("/allIncome")
    public ResponseEntity<Page<Income>> getAllTravelPlanIncome(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllIncomeFromTravelPlan(travelPlanId, pageNumber, pageSize));
    }
    @PutMapping("/new-expenditure")
    public ResponseEntity<List<MonetaryEventEntity>> createExpenditure(
        @RequestBody ExpenditureRequest expenditure
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(expenditure.getParentScheduleId(), conversionService.convert(expenditure,Expenditure.class)));
    }
    @GetMapping("/allExpenditure")
    public ResponseEntity<Page<Expenditure>> getAllTravelPlanExpenditure(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllExpenditureFromTravelPlan(travelPlanId, pageNumber, pageSize));
    }

    @PutMapping("/new-currency-conversion")
    public ResponseEntity<List<MonetaryEventEntity>> createCurrencyConversion(
        @RequestBody CurrencyConvertRequest currencyConversion
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(currencyConversion.getParentScheduleId(), conversionService.convert(currencyConversion,CurrencyConversion.class)));
    }

    @GetMapping("/allCurrencyConversion")
    public ResponseEntity<Page<CurrencyConversion>> getAllTravelPlanCurrencyConversion(UUID travelPlanId, int pageNumber, int pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllCurrencyConversionFromTravelPlan(travelPlanId, pageNumber, pageSize));
    }

    @GetMapping("/allMonetaryEvents")
    public ResponseEntity<Page<MonetaryEvent>> getAllTravelPlanMonetaryEvents(UUID travelPlanId, int pageNumber, int pageSize) {
        Page<MonetaryEvent> monetaryEventsPage = monetaryDomainQueryService.getAllMonetaryEventsInTravelPlan(travelPlanId, pageNumber, pageSize);
        return ResponseEntity.ok(monetaryEventsPage);
    }

    @GetMapping("/allMonetaryEventsBetween")
    public ResponseEntity<Page<MonetaryEvent>> getAllTravelPlanMonetaryEventBetween(UUID travelPlanId, Instant to, Instant from, int pageNumber, int pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllMonetaryEventBetween(travelPlanId, to, from, pageNumber, pageSize));
    }

    @GetMapping("/schedule-income")
    public ResponseEntity<Page<Income>> getAllScheduleIncome(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllIncomeFromSchedule(scheduleId, pageNumber, pageSize));
    }

    @GetMapping("/schedule-expenditure")
    public ResponseEntity<Page<Expenditure>> getAllScheduleExpenditure(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllExpenditureFromSchedule(scheduleId, pageNumber, pageSize));
    }

    @GetMapping("/schedule-currency-conversion")
    public ResponseEntity<Page<CurrencyConversion>> getAllScheduleCurrencyConversion(UUID scheduleId, int pageNumber, int pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllCurrencyConversionFromSchedule(scheduleId, pageNumber, pageSize));
    }

    @GetMapping("/schedule-monetary-events")
    public ResponseEntity<Page<MonetaryEvent>> getAllScheduleMonetaryEvents(UUID scheduleId, int pageNumber, int pageSize) {
        Page<MonetaryEvent> monetaryEventsPage = monetaryDomainQueryService.getAllMonetaryEventsFromSchedule(scheduleId, pageNumber, pageSize);
        return ResponseEntity.ok(monetaryEventsPage);
    }

}
