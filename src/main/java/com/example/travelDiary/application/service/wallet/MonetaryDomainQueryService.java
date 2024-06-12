package com.example.travelDiary.application.service.wallet;

import com.example.travelDiary.application.service.tags.TagsAccessService;
import com.example.travelDiary.domain.model.tags.Tags;
import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.EventType;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.example.travelDiary.repository.persistence.wallet.MonetaryEventEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.travelDiary.domain.model.wallet.mapper.MonetaryEventMapper.toAggregates;

@Service
public class MonetaryDomainQueryService {

    private final MonetaryEventEntityRepository repository;
    private final TagsAccessService tagsAccessService;

    @Autowired
    public MonetaryDomainQueryService(MonetaryEventEntityRepository repository, TagsAccessService tagsAccessService) {
        this.repository = repository;
        this.tagsAccessService = tagsAccessService;
    }

    public List<MonetaryEvent> getAllMonetaryEvents() {
        List<MonetaryEventEntity> entities = repository.findAll();
        return toAggregates(entities);
    }

    public Page<Income> getAllIncome(Integer pageSize, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllIncomes(page);
    }

    public Page<Expenditure> getAllExpenditure(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllExpenditure(page);
    }

    public Page<CurrencyConversion> getAllCurrencyConversion(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllCurrencyConversion(page);
    }

    public List<MonetaryEvent> findAllById(List<UUID> ids) {
        List<MonetaryEventEntity> entity = repository.findAllById(ids);
        return toAggregates(entity);
    }

    public List<MonetaryEvent> convertAllToAggregates(List<MonetaryEventEntity> monetaryEvents) {
        return toAggregates(monetaryEvents);
    }

    public List<Tags> getTagsFromMonetaryEntity(UUID transactionId) {
        MonetaryEventEntity monetaryEventEntity = repository.findById(transactionId).orElseThrow();
        List<Tags> tags = new ArrayList<>();

        switch (monetaryEventEntity.getEventType()) {
            case CURRENCY_CONVERSION:
                tags = tagsAccessService.getTagsForEntity(EventType.CURRENCY_CONVERSION.name(), transactionId);
                break;
            case EXPENDITURE:
                tags = tagsAccessService.getTagsForEntity(EventType.EXPENDITURE.name(), transactionId);
                break;
            case INCOME:
                tags = tagsAccessService.getTagsForEntity(EventType.INCOME.name(), transactionId);
                break;
            default:
                // Handle default case if needed
                break;
        }
        return tags;
    }
}