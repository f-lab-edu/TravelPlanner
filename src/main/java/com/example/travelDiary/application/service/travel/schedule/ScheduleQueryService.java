package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.service.travel.event.ActivityAccessService;
import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;
    private final ActivityAccessService activityAccessService;

    @Autowired
    public ScheduleQueryService(ScheduleRepository scheduleRepository, ActivityAccessService activityAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.activityAccessService = activityAccessService;
    }

    public Schedule getSchedule(UUID scheduleId) {
        return scheduleRepository.getReferenceById(scheduleId);
    }

    public Page<Schedule> getSchedulesOnGivenDay (LocalDate date, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return scheduleRepository.findAllByTravelDate(start, end, pageable);
    }

    public Page<Schedule> getScheduleContainingName(String name, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByPlaceNameContaining(name, pageable);
    }

    public List<MonetaryEvent> getAssociatedMonetaryEvent(UUID scheduleId) {
        List<Activity> activities = scheduleRepository.findById(scheduleId).orElseThrow().getActivities();
        return activities.stream().flatMap(event -> activityAccessService.getAllMonetaryEvents(event.getId()).stream()).toList();
    }

}
