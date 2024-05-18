package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.persistence.location.PlaceRepository;
import com.example.travelDiary.domain.persistence.travel.ScheduleRepository;
import com.example.travelDiary.domain.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.travel.ScheduleInsertRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleAccessService {
    private final ScheduleRepository scheduleRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final EntityManager em;
    private final PlaceRepository placeRepository;
    private final ConversionService conversionService;

    @Autowired
    public ScheduleAccessService(ScheduleRepository scheduleRepository,
                                 TravelPlanRepository travelPlanRepository,
                                 EntityManager em,
                                 PlaceRepository placeRepository,
                                 ConversionService conversionService) {
        this.scheduleRepository = scheduleRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.em = em;
        this.placeRepository = placeRepository;
        this.conversionService = conversionService;
    }
    public Schedule getSchedule(UUID scheduleId) {
        return scheduleRepository.getReferenceById(scheduleId);
    }

    public Page<Schedule> getSchedulesOnGivenDay (LocalDate date, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByTravelDate(date, pageable);
    }

    public Page<Schedule> getScheduleContainingName(String name, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByPlaceNameContaining(name, pageable);
    }

    @Transactional
    public Schedule createSchedule(UUID travelPlanId, ScheduleInsertRequest request) {
        Schedule schedule = conversionService.convert(request, Schedule.class);
        assert schedule != null;

        Place place = schedule.getPlace();
        Optional<Place> existingPlaceOpt = placeRepository.findByGoogleMapsKeyId(place.getGoogleMapsKeyId());

        if (existingPlaceOpt.isPresent()) {
            place = existingPlaceOpt.get();
        } else {
            placeRepository.save(place);
        }
        schedule.setPlace(place);
        schedule = scheduleRepository.save(schedule);
        updateTravelPlanOnInsert(travelPlanId, schedule);

        return schedule;
    }

//    public Schedule modifySchedule(ScheduleInsertRequest request) {
//        Schedule filteredScheduleModifyFrom = conversionService.convert(request, Schedule.class);
//        Schedule scheduleToModify = scheduleRepository.getReferenceById(request.getScheduleId());
//        modifySchedule(scheduleToModify, filteredScheduleModifyFrom);
//
//        return scheduleToModify;
//    }

    @Transactional
    public UUID deleteSchedule(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        Place place = schedule.getPlace();

        scheduleRepository.delete(schedule);

        if (scheduleRepository.findByPlaceId(place.getId()).isEmpty()) {
            placeRepository.deleteById(place.getId());
        }

        return scheduleId;
    }

    @Transactional
    public void reassignPlace(UUID scheduleId, UUID newPlaceId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        Place newPlace = placeRepository.findById(newPlaceId).orElseThrow(() -> new EntityNotFoundException("Place not found"));

        schedule.setPlace(newPlace);
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void updatePlace(Place updatedPlace) {
        Place existingPlace = placeRepository.findById(updatedPlace.getId()).orElseThrow(() -> new EntityNotFoundException("Place not found"));
        existingPlace.setName(updatedPlace.getName());
        existingPlace.setCountry(updatedPlace.getCountry());
        existingPlace.setLatitude(updatedPlace.getLatitude());
        existingPlace.setLongitude(updatedPlace.getLongitude());
        placeRepository.save(existingPlace);

        // Invalidate or recalculate routes if necessary
        List<Schedule> affectedSchedules = scheduleRepository.findByPlaceId(existingPlace.getId());
        for (Schedule schedule : affectedSchedules) {
            schedule.setInwardRoute(null);  // Or trigger recalculation
            schedule.setOutwardRoute(null);  // Or trigger recalculation
            scheduleRepository.save(schedule);
        }
    }

//    UTILS
    private void updateTravelPlanOnInsert(UUID travelPlanId, Schedule scheduleToModify) {
        TravelPlan travelPlan = travelPlanRepository.getReferenceById(travelPlanId);
        List<Schedule> travelPlanSchedule = travelPlan.getScheduleList();
        travelPlanSchedule.add(scheduleToModify);
        travelPlanRepository.save(travelPlan);
    }

    private void modifySchedule(Schedule to, Schedule from) {
        if (from == null || to == null) {
            return;
        }
        em.detach(from);

        // Copy non-null properties from 'from' to 'to'
        to.setPlace(from.getPlace());
        to.setIsActuallyVisited(from.getIsActuallyVisited());
        to.setTravelDate(from.getTravelDate());
        to.setOrderOfTravel(from.getOrderOfTravel());
        to.setTravelStartTimeEstimate(from.getTravelStartTimeEstimate());
        to.setTravelDepartTimeEstimate(from.getTravelDepartTimeEstimate());
        this.flattenPlaceForSchedule(to);
    }

    private void flattenPlaceForSchedule(Schedule schedule) {
        Place place = schedule.getPlace();
        Place existingPlace = place;
        if(schedule.getPlace().getId() != null) {
            existingPlace = placeRepository.findById(schedule.getId()).orElse(place);
        }
        schedule.setPlace(existingPlace);
    }

}
