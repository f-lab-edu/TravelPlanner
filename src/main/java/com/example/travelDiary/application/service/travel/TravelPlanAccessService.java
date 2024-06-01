package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
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

    @Autowired
    public TravelPlanAccessService(TravelPlanRepository travelPlanRepository, ConversionService conversionService, ScheduleQueryService scheduleQueryService) {
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
        this.scheduleQueryService = scheduleQueryService;
    }

    public Page<TravelPlan> getTravelPageContainingName(String name, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return travelPlanRepository.findAllByNameContaining(name, pageRequest);
    }

    public TravelPlan getTravelPlan(UUID planId) {
        return travelPlanRepository.findById(planId).orElseThrow();
    }

    public UUID createPlan(TravelPlanUpsertRequestDTO request) {
        TravelPlan createdPlan = conversionService.convert(request, TravelPlan.class);
        assert createdPlan != null;
        return travelPlanRepository.save(createdPlan).getId();
    }

    public List<UUID> deletePlan(List<UUID> request) {
        travelPlanRepository.deleteAllById(request);
        return request;
    }

    public TravelPlan modifyPlanMetadata(TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = travelPlanRepository.findById(request.getUuid()).orElseThrow();
        updateNonNullFields(request, travelPlan);
        travelPlanRepository.save(travelPlan);
        return travelPlan;
    }

    public TravelPlan importPlan (TravelPlan travelPlan) {
        return travelPlanRepository.save(travelPlan);
    }

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

    public List<TravelPlan> getAllTravelPlan() {
        return travelPlanRepository.findAll();
    }
}
