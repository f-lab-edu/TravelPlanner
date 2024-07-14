package com.roamgram.travelDiary.application.service.travel.schedule;

import com.roamgram.travelDiary.application.service.travel.event.ActivityAccessService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.aop.InjectAuthorisedResourceIds;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.domain.model.travel.Activity;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;
    private final ActivityAccessService activityAccessService;

    @Autowired
    public ScheduleQueryService(ScheduleRepository scheduleRepository, ActivityAccessService activityAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.activityAccessService = activityAccessService;
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Schedule getSchedule(UUID scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow();
    }

    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "Schedule", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<Schedule> getAllAuthorisedSchedulesOnGivenDay(LocalDate date, Integer pageNumber, Integer pageSize, List<UUID> resourceIds) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return scheduleRepository.findAllByTravelDate(start, end, resourceIds, pageable);
    }

    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "Schedule", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<Schedule> getAllAuthorisedScheduleContainingName(String name, Integer pageNumber, Integer pageSize, List<UUID> resourceIds) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByPlaceNameContaining(name, resourceIds, pageable);
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "Schedule", permissionType = UserResourcePermissionTypes.VIEW)
    public List<Schedule> getAllAuthorisedSchedulesInTravelPlan(UUID travelPlanId, List<UUID> resourceIds) {
        return scheduleRepository.findAllByTravelPlanId(travelPlanId, resourceIds);
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public List<MonetaryEvent> getAssociatedMonetaryEvent(UUID scheduleId) {
        List<Activity> activities = scheduleRepository.findById(scheduleId).orElseThrow().getActivities();
        return activities.stream().flatMap(event -> activityAccessService.getAllMonetaryEvents(event.getId()).stream()).toList();
    }

//"    public List<Schedule> getImmediatePrecedingAndSucceedingSchedule(UUID travelPlanId, LocalDateTime
// date) {
//    }"
}
