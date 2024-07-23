package com.roamgram.travelDiary.application.service.wallet;

import com.roamgram.travelDiary.application.service.tags.TagsAccessService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.domain.model.tags.Tags;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.Income;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.EventType;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.roamgram.travelDiary.repository.persistence.wallet.MonetaryEventEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.roamgram.travelDiary.domain.model.wallet.mapper.toaggregate.MonetaryEventAssembler.toAggregates;

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
        return (List<MonetaryEvent>)(toAggregates(entities));
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<Income> getAllIncomeFromTravelPlan(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllIncomeFromTravelPlan(travelPlanId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<Income>) new PageImpl<>(aggregateList, page, entities.getTotalElements());
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<Expenditure> getAllExpenditureFromTravelPlan(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllExpenditureFromTravelPlan(travelPlanId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<Expenditure>) new PageImpl<>(aggregateList, page, entities.getTotalElements());
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<CurrencyConversion> getAllCurrencyConversionFromTravelPlan(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize * 2);
        Page<MonetaryEventEntity> entities = repository.findAllCurrencyConversionFromTravelPlan(travelPlanId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<CurrencyConversion>) new PageImpl<>(aggregateList, page, pageSize);
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<MonetaryEvent> getAllMonetaryEventBetween (UUID travelPlanId, Instant to, Instant from, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize * 2);
        Page<MonetaryEventEntity> entities = repository.findAllMonetaryEventBetweenTimeStampInTravelPlan(travelPlanId, to, from, page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<MonetaryEvent>) new PageImpl<>(aggregateList, page, pageSize);
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<MonetaryEvent> getAllMonetaryEventsInTravelPlan(UUID travelPlanId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize * 2);
        Page<MonetaryEventEntity> entities = repository.findAllMonetaryEventInTravelPlan(travelPlanId, page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<MonetaryEvent>) new PageImpl<>(aggregateList, page, pageSize);
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Income> getAllIncomeFromSchedule(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllIncomeFromSchedule(scheduleId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<Income>) new PageImpl<>(aggregateList, page, entities.getTotalElements());
    }
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Expenditure> getAllExpenditureFromSchedule(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllExpenditureFromSchedule(scheduleId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<Expenditure>) new PageImpl<>(aggregateList, page, entities.getTotalElements());
    }
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<CurrencyConversion> getAllCurrencyConversionFromSchedule(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllCurrencyConversionFromSchedule(scheduleId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<CurrencyConversion>) new PageImpl<>(aggregateList, page, aggregateList.size());
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<MonetaryEvent> getAllMonetaryEventsFromSchedule(UUID scheduleId, Integer pageNumber, Integer pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllMonetaryEventFromSchedule(scheduleId,page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<MonetaryEvent>) new PageImpl<>(aggregateList, page, aggregateList.size());
    }



    public Page<Expenditure> getAllExpenditure(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllExpenditure(page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<Expenditure>) new PageImpl<>(aggregateList, page, aggregateList.size());
    }

    public Page<CurrencyConversion> getAllCurrencyConversion(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<MonetaryEventEntity> entities = repository.findAllCurrencyConversion(page);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities.getContent());
        // Step 4: Convert the List back to a Page
        return (Page<CurrencyConversion>) new PageImpl<>(aggregateList, page, aggregateList.size());
    }

    public List<MonetaryEvent> findAllById(List<UUID> ids) {
        List<MonetaryEventEntity> entities = repository.findAllById(ids);
        List<? extends MonetaryEvent> aggregateList = toAggregates(entities);
        // Step 4: Convert the List back to a Page
        return (List<MonetaryEvent>) aggregateList;
    }

    public List<MonetaryEvent> convertAllToAggregates(List<MonetaryEventEntity> monetaryEvents) {
        return (List<MonetaryEvent>)toAggregates(monetaryEvents);
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