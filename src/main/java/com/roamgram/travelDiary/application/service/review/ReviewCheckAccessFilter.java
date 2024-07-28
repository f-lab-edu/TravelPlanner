package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReviewCheckAccessFilter {

    private final ReviewRepository reviewRepository;
    private final ReviewAccessService reviewAccessService;

    public ReviewCheckAccessFilter(ReviewRepository reviewRepository, ReviewAccessService reviewAccessService) {
        this.reviewRepository = reviewRepository;
        this.reviewAccessService = reviewAccessService;
    }

    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewId", permission = "EDITOR")
    public void shareReviewPermissionFilter(UUID reviewId, UUID userProfileId, String permissionLevel) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        reviewAccessService.shareReview(review, userProfileId, permissionLevel);
    }
}
