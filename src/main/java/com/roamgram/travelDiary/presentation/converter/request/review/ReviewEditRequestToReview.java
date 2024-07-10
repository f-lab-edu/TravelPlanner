package com.roamgram.travelDiary.presentation.converter.request.review;

import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewEditAppendRequest;
import org.springframework.core.convert.converter.Converter;


public class ReviewEditRequestToReview implements Converter<ReviewEditAppendRequest, Review> {

    @Override
    public Review convert(ReviewEditAppendRequest source) {

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