package com.example.travelDiary.application.service.review;

import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.aop.InjectResourceIds;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewAccessService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewAccessService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewId", permission = "EDIT")
    public Review getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow();
    }

    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Review> getAllReviewsFromSchedule(UUID ScheduleId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByScheduleId(ScheduleId, pageable);
    }

    @InjectResourceIds(parameterName = "resourceIds", resourceType = "Review", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<Review> getAllAuthorisedReviews(List<UUID> resourceIds, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findAllAuthorized(resourceIds, pageable);
        return result;
    }

}
