package com.example.travelDiary.service;

import com.example.travelDiary.domain.persistence.travel.TravelPlan;
import com.example.travelDiary.domain.persistence.travel.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelPlanAccessService {
    private final TravelPlanRepository travelPlanRepository;
    private final ScheduleAccessService scheduleAccessService;

    @Autowired
    public TravelPlanAccessService(TravelPlanRepository travelPlanRepository, ScheduleAccessService scheduleAccessService) {
        this.travelPlanRepository = travelPlanRepository;
        this.scheduleAccessService = scheduleAccessService;
    }

    public TravelPlan getTravelPlanWithName(String name) {
        return travelPlanRepository.getByName(name);
    }
}
