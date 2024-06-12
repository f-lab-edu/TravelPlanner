package com.example.travelDiary.application.service;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.TravelPlanCreateRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TravelPlanAccessService {
    private final TravelPlanRepository travelPlanRepository;
    private final ConversionService conversionService;
    private final ScheduleAccessService scheduleAccessService;

    @Autowired
    public TravelPlanAccessService(TravelPlanRepository travelPlanRepository, ConversionService conversionService, ScheduleAccessService scheduleAccessService) {
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
        this.scheduleAccessService = scheduleAccessService;
    }

    public Page<TravelPlan> getTravelPageContainingName(String name, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return travelPlanRepository.findByNameContaining(name, pageRequest);
    }
    public TravelPlan getTravelPlanwithUUID(UUID planId) {
        return travelPlanRepository.getReferenceById(planId);
    }

    public UUID createPlan(TravelPlanCreateRequestDTO request) {
        TravelPlan createdPlan = conversionService.convert(request, TravelPlan.class);
        assert createdPlan != null;
        return travelPlanRepository.save(createdPlan).getId();
    }

}

//   travelPlan/{travelPlanId}/schedule/order/{order_number}
