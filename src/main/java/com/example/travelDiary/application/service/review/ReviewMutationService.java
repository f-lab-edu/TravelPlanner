package com.example.travelDiary.application.service.review;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.review.ReviewCreatedEvent;
import com.example.travelDiary.application.events.review.ReviewDeletedEvent;
import com.example.travelDiary.application.events.review.ReviewPreDeletedEvent;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.service.ResourceService;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.review.ReviewEditRequest;
import com.example.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewMutationService {

    private final ReviewRepository reviewRepository;
    private final ConversionService conversionService;
    private final ResourceService resourceService;
    private final EventPublisher eventPublisher;

    @Autowired
    public ReviewMutationService(ReviewRepository reviewRepository, ConversionService conversionService, ResourceService resourceService, EventPublisher eventPublisher) {
        this.reviewRepository = reviewRepository;
        this.conversionService = conversionService;
        this.resourceService = resourceService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewEditRequest.reviewId", permission = "EDIT")
    public Review editReview(UUID scheduleId, ReviewEditRequest reviewEditRequest) {
        UUID reviewId = reviewEditRequest.getReviewId();
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        updateNonNullFields(reviewEditRequest, review);
        return reviewRepository.save(review);
    }


    public Review updateReviewMediaFile(UUID reviewId, MediaFile mediaFile) {
        Review reviewToUpdate = reviewRepository.findById(reviewId).orElseThrow();
        return reviewRepository.save(reviewToUpdate);
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewId", permission = "EDIT")
    public UUID deleteReview(UUID scheduleId, UUID reviewId) {
        eventPublisher.publishEvent(new ReviewPreDeletedEvent(scheduleId, reviewId));
        reviewRepository.deleteById(reviewId);
        return reviewId;
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    public Review uploadReview(UUID scheduleId, ReviewUploadRequest reviewUploadRequest) {
        Review review = conversionService.convert(reviewUploadRequest, Review.class);
        assert review != null;
        UUID reviewId = reviewRepository.save(review).getId();
        if(reviewUploadRequest.getFileList() != null) {
            for (MediaFile file : reviewUploadRequest.getFileList()){
                file.setReviewId(reviewId);
            }
        }
        review.setFileList(reviewUploadRequest.getFileList());
        review = reviewRepository.save(review);
        eventPublisher.publishEvent(new ReviewCreatedEvent(scheduleId, review));
        return review;
    }

    private static void updateNonNullFields(ReviewEditRequest reviewEditRequest, Review review) {
        if (reviewEditRequest.getFileList() != null) {
            review.setFileList(reviewEditRequest.getFileList());
        }
        if (reviewEditRequest.getFileLocation() != null) {
            review.setContentLocation(reviewEditRequest.getFileLocation());
        }
        if (reviewEditRequest.getRating() != null) {
            review.setRating(reviewEditRequest.getRating());
        }
        if (reviewEditRequest.getUserDescription() != null) {
            review.setUserDescription(reviewEditRequest.getUserDescription());
        }
    }
}
