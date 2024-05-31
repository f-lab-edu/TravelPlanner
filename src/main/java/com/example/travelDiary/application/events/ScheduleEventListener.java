package com.example.travelDiary.application.events;

import com.example.travelDiary.application.events.travel.ScheduleCreatedEvent;
import com.example.travelDiary.application.events.travel.ScheduleDeletedEvent;
import com.example.travelDiary.application.events.travel.ScheduleUpdatedEvent;
import com.example.travelDiary.application.service.travel.place.PlaceMutationService;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.schedule.Schedule;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScheduleEventListener {

    private final ScheduleRepository scheduleRepository;
    private final PlaceMutationService placeMutationService;

    @Autowired
    public ScheduleEventListener(ScheduleRepository scheduleRepository, PlaceMutationService placeMutationService) {
        this.scheduleRepository = scheduleRepository;
        this.placeMutationService = placeMutationService;
    }

    @EventListener
    public void handleScheduleCreatedEvent(ScheduleCreatedEvent event) {
        Schedule schedule = event.getSchedule();

        Place place = placeMutationService.createPlace(schedule.getPlace());
        schedule.setPlace(place);
        scheduleRepository.save(schedule);

        // Additional logic if needed for Schedule creation
    }

    @EventListener
    public void handleScheduleDeletedEvent(ScheduleDeletedEvent event) {
        UUID placeId = event.getPlaceId();
        if (scheduleRepository.findByPlaceId(placeId).isEmpty()) {
            placeMutationService.deletePlace(placeId);
        }
    }

    @EventListener
    public void handleScheduleUpdatedEvent(ScheduleUpdatedEvent event) {
        // Additional logic if needed for Schedule update
    }
}

