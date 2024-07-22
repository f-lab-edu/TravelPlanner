package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.aop.InjectAuthorisedResourceIds;
import com.roamgram.travelDiary.common.permissions.aop.InjectPublicResourceIds;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewAccessService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewAccessService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewId", permission = "EDITOR")
    public Review getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow();
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "VIEW")
    public Page<Review> getAllReviewsFromSchedule(UUID scheduleId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByScheduleId(scheduleId, pageable);
    }

    @Transactional
    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "Review", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<Review> getAllAuthorisedReviews(List<UUID> resourceIds, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findAllFromAuthorizedIds(resourceIds, pageable);
        return result;
    }

    @Transactional
    @InjectPublicResourceIds(parameterName = "resourceIds", resourceType = "Review")
    public Page<Review> getAllPublicReviews(List<UUID> resourceIds, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findAllFromAuthorizedIds(resourceIds, pageable);
        return result;
    }

    @Transactional
    @InjectPublicResourceIds(parameterName = "resourceIds", resourceType = "Review")
    public Page<Review> getAllPublicReviewsFromGoogleMapsId(List<UUID> resourceIds,  String googleMapsId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findAllFromAuthorizedIdsAndPlace(resourceIds, googleMapsId, pageable);
        return result;
    }

}
