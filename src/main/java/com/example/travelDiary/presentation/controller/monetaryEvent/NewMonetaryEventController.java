package com.example.travelDiary.presentation.controller.monetaryEvent;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.example.travelDiary.application.service.wallet.MonetaryDomainMutationService;
import com.example.travelDiary.presentation.dto.request.wallet.CurrencyConvertRequest;
import com.example.travelDiary.presentation.dto.request.wallet.ExpenditureRequest;
import com.example.travelDiary.presentation.dto.request.wallet.IncomeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/monetary")
public class NewMonetaryEventController {
    private final MonetaryDomainMutationService monetaryDomainMutationService;
    private final ConversionService conversionService;

    @Autowired
    public NewMonetaryEventController(MonetaryDomainMutationService monetaryDomainMutationService, ConversionService conversionService) {
        this.monetaryDomainMutationService = monetaryDomainMutationService;
        this.conversionService = conversionService;
    }

    @PostMapping("/income")
    public ResponseEntity<List<MonetaryEventEntity>> createIncome(@RequestBody IncomeRequest income){
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(income,Income.class)));
    }

    @PostMapping("/expenditure")
    public ResponseEntity<List<MonetaryEventEntity>> createExpenditure(
        @RequestBody ExpenditureRequest expenditure
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(expenditure,Expenditure.class)));
    }

    @PostMapping("/currency-conversion")
    public ResponseEntity<List<MonetaryEventEntity>> createCurrencyConversion(
        @RequestBody CurrencyConvertRequest currencyConversion
    ) {
        return ResponseEntity.ok(monetaryDomainMutationService.save(conversionService.convert(currencyConversion,CurrencyConversion.class)));
    }
}
