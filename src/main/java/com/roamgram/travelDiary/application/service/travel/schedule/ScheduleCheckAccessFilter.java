package com.roamgram.travelDiary.application.service.travel.schedule;


import com.roamgram.travelDiary.application.service.review.ReviewAccessService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScheduleCheckAccessFilter {

    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleRepository scheduleRepository;

    public ScheduleCheckAccessFilter(ReviewRepository reviewRepository, ReviewAccessService reviewAccessService, ScheduleQueryService scheduleQueryService, ScheduleRepository scheduleRepository) {
        this.scheduleQueryService = scheduleQueryService;
        this.scheduleRepository = scheduleRepository;
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDITOR")
    public void shareReviewPermissionFilter(UUID scheduleId, UUID userProfileId, String permissionLevel) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        scheduleQueryService.shareSchedule(schedule, userProfileId, permissionLevel);
    }
}
