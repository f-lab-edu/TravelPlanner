package com.example.travelDiary.application.service.wallet.wallet;

import com.example.travelDiary.application.service.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.application.service.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.application.service.wallet.aggregate.Expenditure;
import com.example.travelDiary.application.service.wallet.aggregate.Income;
import com.example.travelDiary.application.service.wallet.entity.MonetaryEventEntity;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MonetaryDomainMutationService {

    private final MonetaryEventEntityRepository monetaryEventEntityRepository;
    private final ConversionService conversionService;

    @Autowired
    public MonetaryDomainMutationService(MonetaryEventEntityRepository monetaryEventEntityRepository, ConversionService conversionService) {
        this.monetaryEventEntityRepository = monetaryEventEntityRepository;
        this.conversionService = conversionService;
    }


    @Transactional
    public List<MonetaryEventEntity> save(MonetaryEvent monetaryEvent) {
        if (monetaryEvent instanceof CurrencyConversion) {
            CurrencyConversion currencyConversion = (CurrencyConversion) monetaryEvent;
            UUID transactionId = UUID.randomUUID();
            Instant now = Instant.now();
            MonetaryEventEntity from = MonetaryEventEntity
                    .builder()
                    .transactionId(transactionId.toString())
                    .source("source")
                    .amount((currencyConversion.getConvertedAmountFrom()).negate())
                    .currency(currencyConversion.getCurrencyFrom())
                    .timestamp(now)
                    .build();
            MonetaryEventEntity to = MonetaryEventEntity
                    .builder()
                    .transactionId(transactionId.toString())
                    .source("source")
                    .amount((currencyConversion.getConvertedAmountTo()))
                    .currency(currencyConversion.getCurrencyTo())
                    .timestamp(now)
                    .build();
            return List.of(
                monetaryEventEntityRepository.save(from),
                monetaryEventEntityRepository.save(to)
            );
        } else if (monetaryEvent instanceof Expenditure) {
            Expenditure expenditure = (Expenditure) monetaryEvent;
            return List.of(
                monetaryEventEntityRepository.save(
                    MonetaryEventEntity
                        .builder()
                        .transactionId(UUID.randomUUID().toString())
                        .source("source")
                        .amount(expenditure.getAmount())
                        .currency(expenditure.getCurrency())
                        .timestamp(Instant.now())
                        .build()
                )
            );
        } else if (monetaryEvent instanceof Income) {
            Income income = (Income) monetaryEvent;
            return List.of(
                monetaryEventEntityRepository.save(
                    MonetaryEventEntity
                        .builder()
                        .transactionId(UUID.randomUUID().toString())
                        .source("source")
                        .amount(income.getAmount())
                        .currency(income.getCurrency())
                        .timestamp(Instant.now())
                        .build()
                )
            );
        } else {
            throw new RuntimeException("instant type not supported");
        }
    }
}
