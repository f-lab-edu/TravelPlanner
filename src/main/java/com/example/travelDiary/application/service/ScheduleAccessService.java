package com.example.travelDiary.application.service;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.persistence.travel.ScheduleRepository;
import com.example.travelDiary.domain.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.ScheduleUpsertRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleAccessService {
    private final ScheduleRepository scheduleRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final ConversionService conversionService;

    @Autowired
    public ScheduleAccessService(ScheduleRepository scheduleRepository, TravelPlanRepository travelPlanRepository, ConversionService conversionService) {
        this.scheduleRepository = scheduleRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
    }

    public List<Schedule> getSchedulesOnGivenDay (LocalDate date) {
        return scheduleRepository.findAllByTravelDate(date);
    }

    public Schedule getSchedule (LocalDate date, Integer orderOfTravel) {
        return scheduleRepository.findByTravelDateAndOrderOfTravel(date, orderOfTravel);
    }

    public void createSchedule(UUID travelPlanId, ScheduleUpsertRequest request) {
        Schedule createdSchedule = conversionService.convert(request, Schedule.class);
        TravelPlan travelPlan = travelPlanRepository.getReferenceById(travelPlanId);
        List<Schedule> travelPlanSchedule = travelPlan.getScheduleList();
        travelPlanSchedule.ge
    }
}
