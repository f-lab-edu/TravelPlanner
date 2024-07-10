package com.roamgram.travelDiary.presentation.converter.request.review;

import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import org.springframework.core.convert.converter.Converter;

public class ReviewUploadRequestToReview implements Converter<ReviewUploadRequest, Review> {
    @Override
    public Review convert(ReviewUploadRequest source) {

        Review review = new Review();
        if (source.getUserDescription() != null) {
            review.setUserDescription(source.getUserDescription());
        }

        if (source.getRating() != null) {
            review.setRating(source.getRating());
        }

        return review;
    }
}
