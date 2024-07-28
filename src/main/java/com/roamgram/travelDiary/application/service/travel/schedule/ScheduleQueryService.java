package com.roamgram.travelDiary.application.service.travel.schedule;

import com.roamgram.travelDiary.application.service.review.ReviewAccessService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.aop.InjectAuthorisedResourceIds;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.roamgram.travelDiary.common.permissions.service.ResourcePermissionService;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.roamgram.travelDiary.repository.persistence.user.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;
    private final UserProfileRepository userProfileRepository;
    private final ResourcePermissionService resourcePermissionService;
    private final ReviewAccessService reviewAccessService;

    @Autowired
    public ScheduleQueryService(ScheduleRepository scheduleRepository, UserProfileRepository userProfileRepository, ResourcePermissionService resourcePermissionService, ReviewAccessService reviewAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.userProfileRepository = userProfileRepository;
        this.resourcePermissionService = resourcePermissionService;
        this.reviewAccessService = reviewAccessService;
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

    @Transactional
    public void shareSchedule(Schedule schedule, UUID userProfileId, String permissionLevel) {
        Schedule scheduleManaged = scheduleRepository.findById(schedule.getId()).orElseThrow();
        Resource resource = scheduleManaged.getResource();
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(() ->
                new ResourceNotFoundException("the user with ID " + userProfileId.toString() +" is not found"));
        resourcePermissionService.assignPermission(UserResourcePermissionTypes.valueOf(permissionLevel.toUpperCase()),
                resource,
                userProfile);

        reviewAccessService.shareReview(schedule.getReview(), userProfileId, permissionLevel);
    }

//"    public List<Schedule> getImmediatePrecedingAndSucceedingSchedule(UUID travelPlanId, LocalDateTime
// date) {
//    }"
}
