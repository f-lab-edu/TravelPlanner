package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.service.travel.event.ActivityAccessService;
import com.example.travelDiary.application.service.travel.event.ActivityMutationService;
import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.event.ActivityMetaDataUpsertRequest;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleActivityService {

    private final ActivityMutationService activityMutationService;
    private final ActivityAccessService activityAccessService;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleActivityService(ActivityMutationService activityMutationService, ActivityAccessService activityAccessService, ScheduleRepository scheduleRepository) {
        this.activityMutationService = activityMutationService;
        this.activityAccessService = activityAccessService;
        this.scheduleRepository = scheduleRepository;
    }


    public Activity addEvent(ActivityMetaDataUpsertRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Activity activity = activityMutationService.createEvent(request);
        schedule.getActivities().add(activity);
        return activity;
    }

    public UUID deleteEvent(UUID eventId) {
        return activityMutationService.deleteEvent(eventId);
    }

    public Activity updateEvent(ActivityMetaDataUpsertRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Activity activity = activityMutationService.updateEventMetaData(request);
        schedule.getActivities().add(activity);
        return activity;
    }

    public Page<MonetaryEvent> getAssociatedMonetaryEventPage(UUID scheduleId, PageRequest pageRequest) {
        List<Activity> activities = scheduleRepository.findById(scheduleId).orElseThrow().getActivities();
        List<MonetaryEvent> monetaryEvents = activities.stream().flatMap(event -> activityAccessService.getAllMonetaryEvents(event.getId()).stream()).toList();
        return new PageImpl<>(monetaryEvents, pageRequest, monetaryEvents.size());
    }



}
