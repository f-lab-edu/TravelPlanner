package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.service.travel.place.PlaceMutationService;
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
import jakarta.persistence.EntityManager;
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
    private final EntityManager em;
    private final PlaceMutationService placeAccessService;
    private final ConversionService conversionService;
    private final RouteAccessService routeAccessService;

    @Autowired
    public ScheduleMutationService(ScheduleRepository scheduleRepository,
                                   TravelPlanRepository travelPlanRepository,
                                   EntityManager em,
                                   PlaceMutationService placeAccessService,
                                   ConversionService conversionService,
                                   RouteAccessService routeAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.em = em;
        this.placeAccessService = placeAccessService;
        this.conversionService = conversionService;
        this.routeAccessService = routeAccessService;
    }

    @Transactional
    public Schedule createSchedule(UUID travelPlanId, ScheduleInsertRequest request) {
        Schedule schedule = conversionService.convert(request, Schedule.class);
        assert schedule != null;

        Place place = placeAccessService.createPlace(schedule.getPlace());
        schedule.setPlace(place);
        schedule = scheduleRepository.save(schedule);
        updateTravelPlanOnInsert(travelPlanId, schedule);

        if(request.getPreviousScheduleId() != null) {
            Schedule previousSchedule = scheduleRepository.getReferenceById(request.getPreviousScheduleId());
            Route route = routeAccessService.createEmptyRoute();
            previousSchedule.setOutwardRoute(route);
            schedule.setInwardRoute(route);
        }

        return schedule;
    }

    @Transactional
    public UUID deleteSchedule(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        Place place = schedule.getPlace();

        scheduleRepository.delete(schedule);

        if (scheduleRepository.findByPlaceId(place.getId()).isEmpty()) {
            placeAccessService.deletePlace(place);
        }

        return scheduleId;
    }


    public Schedule updateScheduleMetadata(ScheduleMetadataUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Schedule sanitizedSchedule = conversionService.convert(request, Schedule.class);

        em.detach(sanitizedSchedule);

        schedule.setIsActuallyVisited(sanitizedSchedule.getIsActuallyVisited());
        schedule.setTravelDate(sanitizedSchedule.getTravelDate());
        schedule.setOrderOfTravel(sanitizedSchedule.getOrderOfTravel());
        schedule.setTravelStartTimeEstimate(sanitizedSchedule.getTravelStartTimeEstimate());
        schedule.setTravelDepartTimeEstimate(sanitizedSchedule.getTravelDepartTimeEstimate());

        return scheduleRepository.save(schedule);
    }

    //PLACE UPDATES
    @Transactional
    public Schedule reassignPlace(UUID scheduleId, PlaceUpdateRequest request) {
        Place place = placeAccessService.createNewPlaceIfNotExists(request);
        assert place != null;
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        schedule.setPlace(place);
        routeAccessService.resetRoute(schedule.getInwardRoute());
        routeAccessService.resetRoute(schedule.getOutwardRoute());
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public List<Schedule> updatePlace(PlaceUpdateRequest updateRequest) {
        Place updatedPlace = placeAccessService.updatePlace(updateRequest);
        // Invalidate or recalculate routes if necessary
        List<Schedule> affectedSchedules = scheduleRepository.findByPlaceId(updatedPlace.getId());
        for (Schedule schedule : affectedSchedules) {

            Route originalInwardRoute = schedule.getInwardRoute();
            Route originalOutwardRoute = schedule.getOutwardRoute();
            routeAccessService.resetRoute(originalInwardRoute);
            routeAccessService.resetRoute(originalOutwardRoute);

            scheduleRepository.save(schedule);
        }

        return affectedSchedules;
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
