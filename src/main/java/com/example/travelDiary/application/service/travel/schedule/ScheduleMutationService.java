package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.travel.ScheduleCreatedEvent;
import com.example.travelDiary.application.events.travel.ScheduleDeletedEvent;
import com.example.travelDiary.application.events.travel.SchedulePreDeletedEvent;
import com.example.travelDiary.application.events.travel.ScheduleUpdatedEvent;
import com.example.travelDiary.application.service.location.PlaceMutationService;
import com.example.travelDiary.application.service.travel.RouteAccessService;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleMutationService {
    private final ScheduleRepository scheduleRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final PlaceMutationService placeMutationService;
    private final ConversionService conversionService;
    private final EventPublisher eventPublisher;
    private final RouteAccessService routeAccessService;

    @Autowired
    public ScheduleMutationService(ScheduleRepository scheduleRepository,
                                   TravelPlanRepository travelPlanRepository,
                                   PlaceMutationService placeMutationService,
                                   ConversionService conversionService, EventPublisher eventPublisher,
                                   RouteAccessService routeAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.placeMutationService = placeMutationService;
        this.conversionService = conversionService;
        this.eventPublisher = eventPublisher;
        this.routeAccessService = routeAccessService;
    }

    @Transactional
    public Schedule createSchedule(UUID travelPlanId, ScheduleInsertRequest request) {
        Schedule schedule = conversionService.convert(request, Schedule.class);
        assert schedule != null;
        schedule.setTravelPlanId(travelPlanId);

        schedule = scheduleRepository.save(schedule);
        updateTravelPlanOnInsert(travelPlanId, schedule);

        if(request.getPreviousScheduleId() != null) {
            Schedule previousSchedule = scheduleRepository.findById(request.getPreviousScheduleId()).orElseThrow();
            Route route = routeAccessService.createEmptyRoute();
            previousSchedule.setOutwardRoute(route);
            schedule.setInwardRoute(route);
        }

        eventPublisher.publishEvent(new ScheduleCreatedEvent(schedule));

        return schedule;
    }

    @Transactional
    public UUID deleteSchedule(UUID travelPlanId, UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        eventPublisher.publishEvent(new SchedulePreDeletedEvent(travelPlanId,scheduleId));
        Place place = schedule.getPlace();
        UUID placeId = place == null ? null : place.getId();
        scheduleRepository.delete(schedule);
        eventPublisher.publishEvent(new ScheduleDeletedEvent(scheduleId, placeId));

        return scheduleId;
    }


    public Schedule updateScheduleMetadata(UUID travelPlanId, ScheduleMetadataUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Schedule sanitizedSchedule = conversionService.convert(request, Schedule.class);

        assert sanitizedSchedule != null;
        schedule.setName(sanitizedSchedule.getName());
        schedule.setDescription(sanitizedSchedule.getDescription());
        schedule.setIsActuallyVisited(sanitizedSchedule.getIsActuallyVisited());
        schedule.setTravelStartTimeEstimate(sanitizedSchedule.getTravelStartTimeEstimate());
        schedule.setTravelDepartTimeEstimate(sanitizedSchedule.getTravelDepartTimeEstimate());

        scheduleRepository.save(schedule);

        eventPublisher.publishEvent(new ScheduleUpdatedEvent(schedule));

        return schedule;
    }

    //PLACE UPDATES
    @Transactional
    public Schedule reassignPlace(UUID scheduleId, PlaceUpdateRequest request) {
        //move to controller
        Place place = placeMutationService.createNewPlaceIfNotExists(request);


        assert place != null;
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        UUID oldPlaceId = schedule.getPlace().getId();
        schedule.setPlace(place);
        routeAccessService.resetRoute(schedule.getInwardRoute());
        routeAccessService.resetRoute(schedule.getOutwardRoute());

        scheduleRepository.save(schedule);

        eventPublisher.publishEvent(new ScheduleUpdatedEvent(schedule));
        eventPublisher.publishEvent(new ScheduleDeletedEvent(scheduleId, oldPlaceId));
        return schedule;
    }

    //ROUTE related
    @Transactional
    public Route updateRouteDetails(RouteUpdateRequest request) {
        return routeAccessService.updateRoute(request);
    }

    @Deprecated
    public Route updateRouteManually(RouteUpdateRequest updateRequest) {
        Schedule inboundSchedule = scheduleRepository
                .findById(
                        updateRequest
                                .getInBoundScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Inbound Schedule not found"));

        Schedule outBoundSchedule = scheduleRepository
                .findById(
                        updateRequest
                                .getOutBoundScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Outbound Schedule not found"));

        Route route = conversionService.convert(updateRequest, Route.class);
        inboundSchedule.setInwardRoute(route);
        outBoundSchedule.setOutwardRoute(route);

        return route;
    }

//    UTILS
    private void updateTravelPlanOnInsert(UUID travelPlanId, Schedule scheduleToModify) {
        TravelPlan travelPlan = travelPlanRepository.getReferenceById(travelPlanId);
        List<Schedule> travelPlanSchedule = travelPlan.getScheduleList();
        travelPlanSchedule.add(scheduleToModify);
        travelPlanRepository.save(travelPlan);
    }

}
