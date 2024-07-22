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
        return toAggregates(entities);
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<Income> getAllIncomeFromTravelPlan(UUID travelPlanId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllIncomeFromTravelPlan(travelPlanId,page);
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<Expenditure> getAllExpenditureFromTravelPlan(UUID travelPlanId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllExpenditureFromTravelPlan(travelPlanId,page);
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<CurrencyConversion> getAllCurrencyConversionFromTravelPlan(UUID travelPlanId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllCurrencyConversionFromTravelPlan(travelPlanId,page);
    }
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<MonetaryEvent> getAllMonetaryEventBetween (UUID travelPlanId, Instant to, Instant from, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllMonetaryEventBetweenTimeStampInTravelPlan(travelPlanId,to, from, page);
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Income> getAllIncomeFromSchedule(UUID scheduleId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllIncomeFromTravelPlan(scheduleId,page);
    }
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Expenditure> getAllExpenditureFromSchedule(UUID scheduleId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllExpenditureFromTravelPlan(scheduleId,page);
    }
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<CurrencyConversion> getAllCurrencyConversionFromSchedule(UUID scheduleId, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllCurrencyConversionFromTravelPlan(scheduleId,page);
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