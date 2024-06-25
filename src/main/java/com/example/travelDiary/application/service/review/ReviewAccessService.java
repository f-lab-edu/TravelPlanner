package com.example.travelDiary.application.service.review;

import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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

}
