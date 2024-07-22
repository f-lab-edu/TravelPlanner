package com.roamgram.travelDiary.presentation.controller.monetaryEvent;

import com.roamgram.travelDiary.application.service.wallet.MonetaryDomainQueryService;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
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

import java.util.List;
import java.util.UUID;

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

    @PostMapping("/new-income")
    public ResponseEntity<List<MonetaryEventEntity>> createIncome(@RequestBody IncomeRequest income){
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(income,Income.class)));
    }

    @GetMapping("/allIncome")
    public ResponseEntity<Page<Income>> getAllTravelPlanIncome(UUID travelPlanId, int pageNumber, int pageSize) {
        return ResponseEntity.ok(monetaryDomainQueryService.getAllIncomeFromTravelPlan(travelPlanId, pageNumber, pageSize));
    }
    @PostMapping("/new-expenditure")
    public ResponseEntity<List<MonetaryEventEntity>> createExpenditure(
        @RequestBody ExpenditureRequest expenditure
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(expenditure,Expenditure.class)));
    }

    @PostMapping("/new-currency-conversion")
    public ResponseEntity<List<MonetaryEventEntity>> createCurrencyConversion(
        @RequestBody CurrencyConvertRequest currencyConversion
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(currencyConversion,CurrencyConversion.class)));
    }
}
