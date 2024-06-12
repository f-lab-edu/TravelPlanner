// PlaceEventListener.java
package com.example.travelDiary.application.events.eventListener;

import com.example.travelDiary.application.events.location.PlaceUpdatedEvent;
import com.example.travelDiary.application.service.travel.RouteAccessService;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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

    @TransactionalEventListener(fallbackExecution=true)
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
