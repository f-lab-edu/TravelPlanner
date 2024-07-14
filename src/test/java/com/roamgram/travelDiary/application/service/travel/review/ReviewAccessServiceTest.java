package com.roamgram.travelDiary.application.service.travel.review;

import com.roamgram.travelDiary.application.service.review.ReviewAccessService;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ReviewAccessServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewAccessService reviewService;


    @Test
    public void testFindAllFromAuthorizedIdsAndPlace() {
        List<UUID> resourceIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        String googleMapsId = "sampleGoogleMapsId";
        Pageable pageable = PageRequest.of(0, 10);

        Review review1 = new Review();
        review1.setId(UUID.randomUUID());
        review1.setUserDescription("Sample Review 1");

        Review review2 = new Review();
        review2.setId(UUID.randomUUID());
        review2.setUserDescription("Sample Review 2");

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review1, review2));
        when(reviewRepository.findAllFromAuthorizedIdsAndPlace(anyList(), anyString(), any(Pageable.class))).thenReturn(reviewPage);

        Page<Review> result = reviewService.getAllPublicReviewsFromGoogleMapsId(resourceIds, googleMapsId, 0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("Sample Review 1", result.getContent().get(0).getUserDescription());
        assertEquals("Sample Review 2", result.getContent().get(1).getUserDescription());
    }

}
