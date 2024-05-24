package com.example.travelDiary.application.service.wallet;

import com.example.travelDiary.domain.model.wallet.entity.EventType;
import com.example.travelDiary.repository.persistence.wallet.MonetaryEventEntityRepository;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

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
        switch (monetaryEvent) {
            case CurrencyConversion currencyConversion -> {
                UUID transactionId = UUID.randomUUID();
                Instant now = Instant.now();
                MonetaryEventEntity from = MonetaryEventEntity
                        .builder()
                        .transactionId(transactionId.toString())
                        .source("source")
                        .amount((currencyConversion.getConvertedAmountFrom()).negate())
                        .currency(currencyConversion.getCurrencyFrom())
                        .timestamp(now)
                        .eventType(EventType.CURRENCY_CONVERSION)
                        .build();
                MonetaryEventEntity to = MonetaryEventEntity
                        .builder()
                        .transactionId(transactionId.toString())
                        .source("source")
                        .amount((currencyConversion.getConvertedAmountTo()))
                        .currency(currencyConversion.getCurrencyTo())
                        .timestamp(now)
                        .eventType(EventType.CURRENCY_CONVERSION)
                        .build();
                return List.of(
                        monetaryEventEntityRepository.save(from),
                        monetaryEventEntityRepository.save(to)
                );
            }
            case Expenditure expenditure -> {
                return List.of(
                        monetaryEventEntityRepository.save(
                                MonetaryEventEntity
                                        .builder()
                                        .transactionId(UUID.randomUUID().toString())
                                        .source("source")
                                        .amount(expenditure.getAmount())
                                        .currency(expenditure.getCurrency())
                                        .timestamp(Instant.now())
                                        .description(expenditure.getDescription())
                                        .eventType(EventType.EXPENDITURE)
                                        .build()
                        )
                );
            }
            case Income income -> {
                return List.of(
                        monetaryEventEntityRepository.save(
                                MonetaryEventEntity
                                        .builder()
                                        .transactionId(UUID.randomUUID().toString())
                                        .source("source")
                                        .amount(income.getAmount())
                                        .currency(income.getCurrency())
                                        .description(income.getDescription())
                                        .timestamp(Instant.now())
                                        .eventType(EventType.INCOME)
                                        .build()
                        )
                );
            }
            case null, default -> throw new RuntimeException("instant type not supported");
        }
    }
}
