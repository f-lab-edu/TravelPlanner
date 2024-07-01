package com.example.travelDiary.application.service.travel.review;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.service.review.MediaFileFacadeService;
import com.example.travelDiary.application.service.review.ReviewMutationService;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.presentation.dto.request.review.ReviewEditAppendRequest;
import com.example.travelDiary.presentation.dto.request.review.ReviewEditRemoveRequest;
import com.example.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import com.example.travelDiary.presentation.dto.response.review.ReviewUploadResponse;
import com.example.travelDiary.repository.persistence.review.MediaFileRepository;
import com.example.travelDiary.repository.persistence.review.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewMutationServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ConversionService conversionService;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private MediaFileFacadeService mediaFileAccessService;
    @Mock
    private MediaFileRepository mediaFileRepository;

    @InjectMocks
    private ReviewMutationService reviewMutationService;

    private ReviewEditAppendRequest reviewEditAppendRequest;
    private ReviewEditRemoveRequest reviewEditRemoveRequest;
    private ReviewUploadRequest reviewUploadRequest;

    @BeforeEach
    public void setUp() {
        reviewEditAppendRequest = new ReviewEditAppendRequest();
        reviewEditRemoveRequest = new ReviewEditRemoveRequest();
        reviewUploadRequest = new ReviewUploadRequest();
    }

    @Test
    public void testEditReviewEditAppendFiles() {
        UUID reviewId = UUID.randomUUID();
        reviewEditAppendRequest.setReviewId(reviewId);
        Review review = new Review();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewMutationService.editReviewEditAppendFiles(reviewEditAppendRequest);
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    public void testEditReviewRemoveFiles() {
        UUID reviewId = UUID.randomUUID();
        reviewEditRemoveRequest.setReviewId(reviewId);
        Review review = new Review();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
//        when(mediaFileRepository.findByS3Key(anyString())).thenReturn(new MediaFile());

        Review result = reviewMutationService.editReviewRemoveFiles(reviewEditRemoveRequest);
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    public void testDeleteReview() {
        UUID reviewId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        doNothing().when(eventPublisher).publishEvent(any());
        doNothing().when(reviewRepository).deleteById(any(UUID.class));

        UUID result = reviewMutationService.deleteReview(scheduleId, reviewId);
        assertNotNull(result);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    public void testUploadReview() {
        UUID scheduleId = UUID.randomUUID();
        Review review = new Review();
        when(conversionService.convert(any(ReviewUploadRequest.class), eq(Review.class))).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewUploadResponse response = reviewMutationService.uploadReview(scheduleId, reviewUploadRequest);
        assertNotNull(response);
        verify(reviewRepository).save(any(Review.class));
    }
}
