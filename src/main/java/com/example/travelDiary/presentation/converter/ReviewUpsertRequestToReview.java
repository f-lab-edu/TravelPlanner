package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.presentation.dto.review.ReviewUpsertRequest;
import org.springframework.core.convert.converter.Converter;

public class ReviewUpsertRequestToReview implements Converter<ReviewUpsertRequest, Review> {
    @Override
    public Review convert(ReviewUpsertRequest source) {

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
