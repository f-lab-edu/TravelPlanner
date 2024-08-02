// PlaceEventListener.java
package com.roamgram.travelDiary.application.events.eventListener;

import com.roamgram.travelDiary.application.events.location.PlaceUpdatedEvent;
import com.roamgram.travelDiary.application.service.travel.RouteAccessService;
import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.travel.Route;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class PlaceEventListener {

    private final ScheduleRepository scheduleRepository;
    private final RouteAccessService routeAccessService;

    @Autowired
    public PlaceEventListener(ScheduleRepository scheduleRepository, RouteAccessService routeAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.routeAccessService = routeAccessService;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void handlePlaceUpdatedEvent(PlaceUpdatedEvent event) {
        Place updatedPlace = event.getPlace();
        List<Schedule> schedules = scheduleRepository.findByPlaceId(updatedPlace.getId());
        for (Schedule schedule : schedules) {
            Route originalInwardRoute = schedule.getInwardRoute();
            Route originalOutwardRoute = schedule.getOutwardRoute();
            routeAccessService.resetRoute(originalInwardRoute);
            routeAccessService.resetRoute(originalOutwardRoute);

            schedule.setPlace(updatedPlace);
            scheduleRepository.save(schedule);
        }
    }
}
