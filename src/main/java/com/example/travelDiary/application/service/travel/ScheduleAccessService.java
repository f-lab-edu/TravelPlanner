package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.persistence.travel.ScheduleRepository;
import com.example.travelDiary.domain.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.travel.ScheduleUpsertRequest;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleAccessService {
    private final ScheduleRepository scheduleRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final EntityManager em;
    private final ConversionService conversionService;

    @Autowired
    public ScheduleAccessService(ScheduleRepository scheduleRepository,
                                 TravelPlanRepository travelPlanRepository,
                                 EntityManager em,
                                 ConversionService conversionService) {
        this.scheduleRepository = scheduleRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.em = em;
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

    public Schedule createSchedule(UUID travelPlanId, ScheduleUpsertRequest request) {
        Schedule createdSchedule = conversionService.convert(request, Schedule.class);
        assert createdSchedule != null;
        scheduleRepository.save(createdSchedule);
        updateTravelPlanOnInsert(travelPlanId, createdSchedule);

        return createdSchedule;
    }

    public Schedule modifySchedule(ScheduleUpsertRequest request) {
        Schedule filteredScheduleModifyFrom = conversionService.convert(request, Schedule.class);
        Schedule scheduleToModify = scheduleRepository.getReferenceById(request.getScheduleId());
        modifySchedule(scheduleToModify, filteredScheduleModifyFrom);

        return scheduleToModify;
    }

    public UUID deleteSchedule(UUID scheduleId) {
        scheduleRepository.deleteById(scheduleId);
        return scheduleId;
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
    }

}
