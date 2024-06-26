package com.example.travelDiary.presentation.controller.review;

import com.example.travelDiary.application.service.review.ReviewAccessService;
import com.example.travelDiary.application.service.review.ReviewMutationService;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.presentation.dto.request.review.ReviewEditRequest;
import com.example.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/travelPlan/{travelPlanId}/schedule/{scheduleId}")
public class ReviewController {
    private final ReviewAccessService reviewAccessService;
    private final ReviewMutationService reviewMutationService;


    public ReviewController(ReviewAccessService reviewAccessService, ReviewMutationService reviewMutationService) {
        this.reviewAccessService = reviewAccessService;
        this.reviewMutationService = reviewMutationService;
    }

    @GetMapping("/review/get")
    public Review getReview(@PathVariable("travelPlanId") UUID travelPlanId,
                            @PathVariable("scheduleId") UUID scheduleId,
                            @RequestParam("reviewId") UUID reviewId) {

        return reviewAccessService.getReviewById(reviewId);
    }

    @GetMapping("/review/all")
    public Page<Review> getAllReviewFromSchedule(@PathVariable("travelPlanId") UUID travelPlanId,
                                                 @PathVariable("scheduleId") UUID scheduleId,
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam("size") Integer size) {

        return reviewAccessService.getAllReviewsFromSchedule(scheduleId, page, size);
    }

    @PutMapping("/review/upload")
    public Review uploadReview(@PathVariable("travelPlanId") UUID travelPlanId,
                                                 @PathVariable("scheduleId") UUID scheduleId,
                                                 @RequestBody ReviewUploadRequest reviewUploadRequest) {


        return reviewMutationService.uploadReview(scheduleId, reviewUploadRequest);
    }

    @PatchMapping("/review/edit")
    public Review editReview(@PathVariable("travelPlanId") UUID travelPlanId,
                               @PathVariable("scheduleId") UUID scheduleId,
                               @RequestBody ReviewEditRequest reviewEditRequest) {

        return reviewMutationService.editReview(scheduleId, reviewEditRequest);
    }

    @DeleteMapping("/review/delete")
    public UUID deleteReview(@PathVariable("travelPlanId") UUID travelPlanId,
                               @PathVariable("scheduleId") UUID scheduleId,
                               @RequestParam("reviewID") UUID reviewId) {


        return reviewMutationService.deleteReview(scheduleId , reviewId);
    }




}
