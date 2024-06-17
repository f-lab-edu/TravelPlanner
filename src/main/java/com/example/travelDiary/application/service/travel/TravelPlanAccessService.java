package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.permission.ResourceCreationEvent;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.aop.FilterResultsForUser;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TravelPlanAccessService {
    private final TravelPlanRepository travelPlanRepository;
    private final ConversionService conversionService;
    private final ScheduleQueryService scheduleQueryService;
    private final EventPublisher eventPublisher;

    @Autowired
    public TravelPlanAccessService(TravelPlanRepository travelPlanRepository, ConversionService conversionService, ScheduleQueryService scheduleQueryService, EventPublisher eventPublisher) {
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
        this.scheduleQueryService = scheduleQueryService;
        this.eventPublisher = eventPublisher;
    }

    @FilterResultsForUser(resourceType = TravelPlan.class, permission = "VIEW")
    public Page<TravelPlan> getTravelPageContainingName(String name, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return travelPlanRepository.findAllByNameContaining(name, pageRequest);
    }

    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#planId", permission = "VIEW")
    public TravelPlan getTravelPlan(UUID planId) {
        return travelPlanRepository.findById(planId).orElseThrow();
    }

    public UUID createPlan(TravelPlanUpsertRequestDTO request) {
        TravelPlan createdPlan = conversionService.convert(request, TravelPlan.class);
        assert createdPlan != null;
        TravelPlan travelPlan = travelPlanRepository.save(createdPlan);
        eventPublisher.publishEvent(new ResourceCreationEvent(travelPlan, "private"));
        return travelPlan.getId();
    }

    @FilterResultsForUser(resourceType = TravelPlan.class, permission = "EDIT")
    public List<UUID> deletePlan(List<UUID> request) {
        travelPlanRepository.deleteAllById(request);

        return request;
    }

    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#request.uuid", permission = "EDIT")
    public TravelPlan modifyPlanMetadata(TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = travelPlanRepository.findById(request.getUuid()).orElseThrow();
        updateNonNullFields(request, travelPlan);
        travelPlanRepository.save(travelPlan);
        return travelPlan;
    }


    public TravelPlan importPlan (TravelPlan travelPlan) {
        return travelPlanRepository.save(travelPlan);
    }

    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#travelPlanId", permission = "VIEW")
    @FilterResultsForUser(resourceType = MonetaryEvent.class, permission = "VIEW")
    public Page<MonetaryEvent> getAssociatedMonetaryEvent(UUID travelPlanId, PageRequest pageRequest) {
        List<MonetaryEvent> monetaryEvents = travelPlanRepository
                .findById(travelPlanId)
                .orElseThrow()
                .getScheduleList()
                .stream()
                .flatMap(schedule -> scheduleQueryService
                        .getAssociatedMonetaryEvent(
                                schedule
                                        .getId()
                        )
                        .stream()
                )
                .toList();

        return new PageImpl<>(monetaryEvents, pageRequest, monetaryEvents.size());

    }

    private void updateNonNullFields(TravelPlanUpsertRequestDTO request, TravelPlan travelPlan) {

        if (request.getEndDate() != null) {
            travelPlan.setTravelEndDate(request.getEndDate());
        }
        if (request.getStartDate() != null) {
            travelPlan.setTravelStartDate(request.getStartDate());
        }
        if (request.getName() != null) {
            travelPlan.setName(request.getName());
        }
    }

    @FilterResultsForUser(resourceType = TravelPlan.class, permission = "VIEW")
    public List<TravelPlan> getAllTravelPlan() {
        return travelPlanRepository.findAll();
    }
}
