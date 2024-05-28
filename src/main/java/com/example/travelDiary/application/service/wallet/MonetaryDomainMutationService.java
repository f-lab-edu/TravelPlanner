package com.example.travelDiary.application.service.wallet;

import com.example.travelDiary.application.service.tags.TagsAccessService;
import com.example.travelDiary.domain.model.tags.Tags;
import com.example.travelDiary.domain.model.wallet.entity.EventType;
import com.example.travelDiary.repository.persistence.wallet.MonetaryEventEntityRepository;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.example.travelDiary.domain.model.wallet.entity.EventType.*;

@Service
public class MonetaryDomainMutationService {

    private final MonetaryEventEntityRepository monetaryEventEntityRepository;
    private final ConversionService conversionService;
    private final TagsAccessService tagsAccessService;

    @Autowired
    public MonetaryDomainMutationService(MonetaryEventEntityRepository monetaryEventEntityRepository, ConversionService conversionService, TagsAccessService tagsAccessService) {
        this.monetaryEventEntityRepository = monetaryEventEntityRepository;
        this.conversionService = conversionService;
        this.tagsAccessService = tagsAccessService;
    }

    @Transactional
    public void addTag(UUID transactionId, Tags tag){
        MonetaryEventEntity monetaryEventEntity = monetaryEventEntityRepository.findById(transactionId).orElseThrow();
        UUID monetaryEventEntityId = monetaryEventEntity.getId();
        switch (monetaryEventEntity.getEventType()) {
            case CURRENCY_CONVERSION:
                tagsAccessService.addTagToEntity(tag.getId(), CURRENCY_CONVERSION.name(), monetaryEventEntityId);
                break;
            case EXPENDITURE:
                tagsAccessService.addTagToEntity(tag.getId(), EXPENDITURE.name(), monetaryEventEntityId);
                break;
            case INCOME:
                tagsAccessService.addTagToEntity(tag.getId(), INCOME.name(), monetaryEventEntityId);
                break;
            default:
                // Handle default case if needed
                break;
        }
    }

    @Transactional
    public void deleteTags(UUID transactionId, Tags tag) {
        MonetaryEventEntity monetaryEventEntity = monetaryEventEntityRepository.findById(transactionId).orElseThrow();
        UUID monetaryEventEntityId = monetaryEventEntity.getId();
        tagsAccessService.deleteTagFromEntity(tag.getId(), monetaryEventEntityId);
    }

    @Transactional
    public void delete(String transactionId) {
        List<MonetaryEventEntity> deletable = monetaryEventEntityRepository.findAllByTransactionId(transactionId);
        monetaryEventEntityRepository.deleteAll(deletable);
    }

//    only same type updates are possible
    @Transactional
    public List<MonetaryEventEntity> update(String transactionId, MonetaryEvent update) {
        List<MonetaryEventEntity> updateList = monetaryEventEntityRepository.findAllByTransactionId(transactionId);
        List<MonetaryEventEntity> updateConverted = monetaryEventToEntity(update);
        if(updateList.size() != updateConverted.size()) {
            throw new RuntimeException("the entities created do not match for update");
        }
        IntStream.range(0, updateList.size())
                .map(idx -> {
                    MonetaryEventEntity original = updateList.get(idx);
                    MonetaryEventEntity updateEntity = updateConverted.get(idx);
                    original.setAmount(updateEntity.getAmount());
                    original.setCurrency(updateEntity.getCurrency());
                    original.setConversionRate(updateEntity.getConversionRate());
                    original.setDescription(updateEntity.getDescription());
                    original.setSource(updateEntity.getSource());
                    original.setTimestamp(updateEntity.getTimestamp());
                    return idx;
                });
        return monetaryEventEntityRepository.saveAll(updateList);
    }

    @Transactional
    public List<MonetaryEventEntity> save(MonetaryEvent monetaryEvent) {
        List<MonetaryEventEntity> convertedList = monetaryEventToEntity(monetaryEvent);
        return monetaryEventEntityRepository.saveAll(convertedList);
    }

    private @NotNull List<MonetaryEventEntity> monetaryEventToEntity(MonetaryEvent monetaryEvent) {
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
                        .eventType(CURRENCY_CONVERSION)
                        .build();
                MonetaryEventEntity to = MonetaryEventEntity
                        .builder()
                        .transactionId(transactionId.toString())
                        .source("source")
                        .amount((currencyConversion.getConvertedAmountTo()))
                        .currency(currencyConversion.getCurrencyTo())
                        .timestamp(now)
                        .eventType(CURRENCY_CONVERSION)
                        .build();
                return List.of(from, to);
            }
            case Expenditure expenditure -> {
                return List.of(
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

                );
            }
            case Income income -> {
                return List.of(
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
                );
            }
            case null, default -> throw new RuntimeException("instant type not supported");
        }
    }
}
