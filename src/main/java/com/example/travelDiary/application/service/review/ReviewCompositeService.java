package com.example.travelDiary.application.service.review;


import com.example.travelDiary.application.service.travel.TravelPlanQueryService;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewCompositeService {

    private final ReviewRepository reviewRepository;
    private final TravelPlanQueryService travelPlanQueryService;
    private final ScheduleQueryService scheduleQueryService;

    @Autowired
    public ReviewCompositeService(ReviewRepository reviewRepository, TravelPlanQueryService travelPlanQueryService, ScheduleQueryService scheduleQueryService) {
        this.reviewRepository = reviewRepository;
        this.travelPlanQueryService = travelPlanQueryService;
        this.scheduleQueryService = scheduleQueryService;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
    public Page<Review> getAllReviewsFromTravelPlan(UUID travelPlanId, Integer page, Integer size) {
        List<Schedule> schedules = scheduleQueryService.getAllAuthorisedSchedulesInTravelPlan(travelPlanId,null);
        List<UUID> scheduleIds = schedules.stream().map(Schedule::getId).toList();
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.getAllReviewsFromScheduleIds(scheduleIds, pageable);
    }
}
