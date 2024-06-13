package com.example.travelDiary.application.events.eventListener;

import com.example.travelDiary.application.events.travel.ScheduleCreatedEvent;
import com.example.travelDiary.application.events.travel.ScheduleDeletedEvent;
import com.example.travelDiary.application.events.travel.SchedulePreDeletedEvent;
import com.example.travelDiary.application.events.travel.ScheduleUpdatedEvent;
import com.example.travelDiary.application.service.location.PlaceMutationService;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.repository.persistence.location.PlaceRepository;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@Slf4j
public class ScheduleEventListener {

    private final ScheduleRepository scheduleRepository;
    private final PlaceMutationService placeMutationService;
    private final PlaceRepository placeRepository;
    private final TravelPlanRepository travelPlanRepository;

    @Autowired
    public ScheduleEventListener(ScheduleRepository scheduleRepository, PlaceMutationService placeMutationService, PlaceRepository placeRepository, TravelPlanRepository travelPlanRepository) {
        this.scheduleRepository = scheduleRepository;
        this.placeMutationService = placeMutationService;
        this.placeRepository = placeRepository;
        this.travelPlanRepository = travelPlanRepository;
    }

    @TransactionalEventListener(fallbackExecution=true)
    public void handleScheduleCreatedEvent(ScheduleCreatedEvent event) {
        Schedule schedule = event.getSchedule();
        if(schedule.getPlace() != null) {
            Place place = placeMutationService.createPlace(schedule.getPlace());
            schedule.setPlace(place);
        }
        scheduleRepository.save(schedule);

        // Additional logic if needed for Schedule creation
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSchedulePreDeletedEvent(SchedulePreDeletedEvent event) {
        log.info("prehandler is published");
        TravelPlan travelPlan = travelPlanRepository.findById(event.getTravelPlanId()).orElseThrow();
        travelPlan.getScheduleList().remove(scheduleRepository.findById(event.getScheduleId()).orElseThrow());
        travelPlanRepository.save(travelPlan);
    }

    @EventListener()
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleScheduleDeletedEvent(ScheduleDeletedEvent event) {
        log.info("postHandler is published");
        UUID placeId = event.getPlaceId();
        if(placeId == null) {
            return;
        }
        if (scheduleRepository.findByPlaceId(placeId).isEmpty()) {
            placeMutationService.deletePlace(placeId);
        }
    }



    @EventListener
    public void handleScheduleUpdatedEvent(ScheduleUpdatedEvent event) {
        // Additional logic if needed for Schedule update
    }
}

