package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.schedule.ScheduleCreatedEvent;
import com.example.travelDiary.application.events.schedule.ScheduleDeletedEvent;
import com.example.travelDiary.application.events.schedule.SchedulePreDeletedEvent;
import com.example.travelDiary.application.events.schedule.ScheduleUpdatedEvent;
import com.example.travelDiary.application.service.location.PlaceMutationService;
import com.example.travelDiary.application.service.travel.RouteAccessService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ScheduleMutationService {
    private final ScheduleRepository scheduleRepository;
    private final PlaceMutationService placeMutationService;
    private final ConversionService conversionService;
    private final EventPublisher eventPublisher;
    private final RouteAccessService routeAccessService;

    @Autowired
    public ScheduleMutationService(ScheduleRepository scheduleRepository,
                                   PlaceMutationService placeMutationService,
                                   ConversionService conversionService,
                                   EventPublisher eventPublisher,
                                   RouteAccessService routeAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.placeMutationService = placeMutationService;
        this.conversionService = conversionService;
        this.eventPublisher = eventPublisher;
        this.routeAccessService = routeAccessService;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "EDIT")
    public UUID createSchedule(UUID travelPlanId, ScheduleInsertRequest request) {
        Schedule schedule = conversionService.convert(request, Schedule.class);
        assert schedule != null;
        schedule.setTravelPlanId(travelPlanId);
        schedule = scheduleRepository.save(schedule);

        if(request.getPreviousScheduleId() != null) {
            Schedule previousSchedule = scheduleRepository.findById(request.getPreviousScheduleId()).orElseThrow();
            Route route = routeAccessService.createEmptyRoute();
            previousSchedule.setOutwardRoute(route);
            schedule.setInwardRoute(route);
        }
        eventPublisher.publishEvent(new ScheduleCreatedEvent(schedule, travelPlanId, request.getPlace()));
        return schedule.getId();
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    public UUID deleteSchedule(UUID travelPlanId, UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        eventPublisher.publishEvent(new SchedulePreDeletedEvent(travelPlanId,scheduleId));
        Place place = schedule.getPlace();
        UUID placeId = place == null ? null : place.getId();
        scheduleRepository.delete(schedule);
        eventPublisher.publishEvent(new ScheduleDeletedEvent(scheduleId, placeId));

        return scheduleId;
    }


    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#request.scheduleId", permission = "EDIT")
    public Schedule updateScheduleMetadata(UUID travelPlanId, ScheduleMetadataUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        updateNonNullMetaDataFields(request, schedule);
        scheduleRepository.save(schedule);
        eventPublisher.publishEvent(new ScheduleUpdatedEvent(schedule, travelPlanId));

        return schedule;
    }


    //PLACE UPDATES
    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
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
        scheduleRepository.flush();
        eventPublisher.publishEvent(new ScheduleUpdatedEvent(schedule, schedule.travelPlanId));
        eventPublisher.publishEvent(new ScheduleDeletedEvent(scheduleId, oldPlaceId));
        return schedule;
    }

    //ROUTE related
    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#updateRequest.outBoundScheduleId", permission = "EDIT")
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#updateRequest.inBoundScheduleId", permission = "EDIT")
    public Route updateRouteDetails(RouteUpdateRequest updateRequest) {
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

        Route route = routeAccessService.updateRoute(updateRequest);
        inboundSchedule.setInwardRoute(route);
        outBoundSchedule.setOutwardRoute(route);
        return route;
    }



//    Todo
    @Transactional
    public void importSchedule(TravelPlan importedTravelPlan) {
        return;
    }


    private static void updateNonNullMetaDataFields(ScheduleMetadataUpdateRequest request, Schedule schedule) {
        if (request.getName() != null) {
            schedule.setName(request.getName());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }
        if (request.getIsActuallyVisited() != null) {
            schedule.setIsActuallyVisited(request.getIsActuallyVisited());
        }
        if (request.getTravelStartTimeEstimate() != null) {
            schedule.setTravelStartTimeEstimate(request.getTravelStartTimeEstimate());
        }
        if (request.getTravelDepartTimeEstimate() != null) {
            schedule.setTravelDepartTimeEstimate(request.getTravelDepartTimeEstimate());
        }
    }

    @Transactional
    public void linkReview(UUID scheduleId, Review review) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        schedule.setReview(review);
    }

    @Transactional
    public void removeReview(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        schedule.setReview(null);
    }
}
