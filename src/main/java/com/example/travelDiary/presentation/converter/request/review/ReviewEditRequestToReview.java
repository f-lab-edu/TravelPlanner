package com.example.travelDiary.presentation.converter.request.review;

import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.presentation.dto.request.review.ReviewEditRequest;
import org.springframework.core.convert.converter.Converter;


public class ReviewEditRequestToReview implements Converter<ReviewEditRequest, Review> {

    @Override
    public Review convert(ReviewEditRequest source) {

        Review review = new Review();

        if (source.getUserDescription() != null) {
            review.setUserDescription(source.getUserDescription());
        }

        if(source.getFileList() != null || !source.getFileList().isEmpty()) {
            review.setFileList(source.getFileList());
        }

        if(source.getFileLocation() != null || !source.getFileLocation().isEmpty()) {
            review.setContentLocation(source.getFileLocation());
        }

        if (source.getRating() != null) {
            review.setRating(source.getRating());
        }

        return review;
    }
}