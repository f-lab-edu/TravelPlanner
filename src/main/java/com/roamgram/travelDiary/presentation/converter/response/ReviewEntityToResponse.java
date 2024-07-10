package com.roamgram.travelDiary.presentation.converter.response;

import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.presentation.dto.response.review.ReviewResponse;
import org.springframework.core.convert.converter.Converter;

public class ReviewEntityToResponse implements Converter<Review, ReviewResponse> {
    @Override
    public ReviewResponse convert(Review source) {
        if (source == null) {
            return null;
        }
        ReviewResponse dto = new ReviewResponse();
        dto.setId(source.getId());
        dto.setRating(source.getRating());
        dto.setContentLocation(source.getContentLocation());
        dto.setFileList(source.getFileList());
        dto.setUserDescription(dto.getUserDescription());
        dto.setScheduleId(dto.getScheduleId());

        return dto;
    }
}
