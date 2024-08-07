package com.roamgram.travelDiary.presentation.controller.review;

import com.roamgram.travelDiary.application.service.review.ReviewAccessService;
import com.roamgram.travelDiary.application.service.review.ReviewMutationService;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewEditAppendRequest;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewEditRemoveRequest;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import com.roamgram.travelDiary.presentation.dto.response.review.ReviewResponse;
import com.roamgram.travelDiary.presentation.dto.response.review.ReviewUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan/{travelPlanId}/schedule/{scheduleId}")
@Slf4j
public class ReviewController {

    private final ReviewAccessService reviewAccessService;
    private final ReviewMutationService reviewMutationService;
    private final ConversionService conversionService;

    @Autowired
    public ReviewController(ReviewAccessService reviewAccessService, ReviewMutationService reviewMutationService, ConversionService conversionService) {
        this.reviewAccessService = reviewAccessService;
        this.reviewMutationService = reviewMutationService;
        this.conversionService = conversionService;
    }

    @GetMapping("/review/get")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable UUID travelPlanId,
                                            @PathVariable UUID scheduleId,
                                            @RequestParam UUID reviewId) {
        Review review = reviewAccessService.getReviewById(reviewId);
        ReviewResponse reviewResponse = conversionService.convert(review, ReviewResponse.class);
        return ResponseEntity.ok(conversionService.convert(review, ReviewResponse.class));
    }

    @GetMapping("/review/schedule-all")
    public ResponseEntity<Page<ReviewResponse>> getAllReviewFromSchedule(@PathVariable UUID travelPlanId,
                                                                         @PathVariable UUID scheduleId,
                                                                         @RequestParam Integer page,
                                                                         @RequestParam Integer size) {
        Page<ReviewResponse> reviews = reviewAccessService.getAllReviewsFromSchedule(scheduleId, page, size)
                .map(review -> conversionService.convert(review, ReviewResponse.class));
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/review/public-all")
    public ResponseEntity<Page<ReviewResponse>> getReview(@PathVariable UUID travelPlanId,
                                                    @PathVariable UUID scheduleId,
                                                    @RequestParam Integer page,
                                                    @RequestParam Integer size) {
        Page<ReviewResponse> reviews = reviewAccessService.getAllPublicReviews(new ArrayList<>(), page, size)
                .map(review -> conversionService.convert(review, ReviewResponse.class));
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/review/public-google-maps-id")
    public ResponseEntity<Page<ReviewResponse>> getReview(@PathVariable UUID travelPlanId,
                                                          @PathVariable UUID scheduleId,
                                                          @RequestParam Integer page,
                                                          @RequestParam Integer size,
                                                          @RequestParam String googleMapsId) {
        Page<ReviewResponse> reviews = reviewAccessService.getAllPublicReviewsFromGoogleMapsId(new ArrayList<>(), googleMapsId, page, size)
                .map(review -> conversionService.convert(review, ReviewResponse.class));
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/review/upload")
    public ResponseEntity<ReviewUploadResponse> uploadReview(@PathVariable UUID travelPlanId,
                                                             @PathVariable UUID scheduleId,
                                                             @RequestBody ReviewUploadRequest reviewUploadRequest) {
        ReviewUploadResponse result = reviewMutationService.uploadReview(scheduleId, reviewUploadRequest);
        return result.getPendingOrFailedFiles().isEmpty()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
    }

    @PatchMapping("/review/editAppendFiles")
    public ResponseEntity<ReviewResponse> editReviewAppendFiles(@PathVariable UUID travelPlanId,
                                                        @PathVariable UUID scheduleId,
                                                        @RequestBody ReviewEditAppendRequest reviewEditAppendRequest) {
        Review updatedReview = reviewMutationService.editReviewAppendFiles(reviewEditAppendRequest);
        return ResponseEntity.ok(conversionService.convert(updatedReview, ReviewResponse.class));
    }

    @PatchMapping("/review/editRemoveFiles")
    public ResponseEntity<ReviewResponse> editReviewRemoveFiles(@PathVariable UUID travelPlanId,
                                                        @PathVariable UUID scheduleId,
                                                        @RequestBody ReviewEditRemoveRequest reviewEditRemoveRequest) {
        Review updatedReview = reviewMutationService.editReviewRemoveFiles(reviewEditRemoveRequest);
        return ResponseEntity.ok(conversionService.convert(updatedReview, ReviewResponse.class));
    }

    @DeleteMapping("/review/delete")
    public ResponseEntity<UUID> deleteReview(@PathVariable UUID travelPlanId,
                                             @PathVariable UUID scheduleId,
                                             @RequestParam UUID reviewID) {
        UUID deletedReviewId = reviewMutationService.deleteReview(scheduleId, reviewID);
        return ResponseEntity.ok(deletedReviewId);
    }
}
