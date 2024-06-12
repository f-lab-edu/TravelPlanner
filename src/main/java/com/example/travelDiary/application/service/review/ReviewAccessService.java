package com.example.travelDiary.application.service.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import com.example.travelDiary.presentation.dto.request.review.ReviewUpsertRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ReviewAccessService {
    private final MediaFileAccessService mediaFileAccessService;
    private final ConversionService conversionService;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewAccessService(MediaFileAccessService mediaFileAccessService, ConversionService conversionService, ReviewRepository reviewRepository) {
        this.mediaFileAccessService = mediaFileAccessService;
        this.conversionService = conversionService;
        this.reviewRepository = reviewRepository;
    }

    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    public Page<Review> getAllReviewsFromSchedule(UUID ScheduleId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByScheduleId(ScheduleId, pageable);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        Review reviewToUpdate = reviewRepository.findById(review.getId()).orElseThrow();
        return reviewRepository.save(review);
    }

    public Review updateReviewMediaFile(UUID reviewId, MediaFile mediaFile) {
        Review reviewToUpdate = reviewRepository.findById(reviewId).orElseThrow();
        return reviewRepository.save(reviewToUpdate);
    }

    public UUID deleteReview(UUID id) {
        reviewRepository.deleteById(id);
        return id;
    }

    public Review uploadReview(ReviewUpsertRequest reviewUploadRequest) {
        Review review = conversionService.convert(reviewUploadRequest, Review.class);
        assert review != null;
        UUID reviewId = reviewRepository.save(review).getId();
        if(reviewUploadRequest.getFileList() != null) {
            for (MediaFile file : reviewUploadRequest.getFileList()){
                file.setReviewId(reviewId);
            }
        }
        review.setFileList(reviewUploadRequest.getFileList());
        return reviewRepository.save(review);
    }
}
