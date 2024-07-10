package com.roamgram.travelDiary.presentation.dto.request.review;

import com.roamgram.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ReviewEditAppendRequest {

    public UUID reviewId;

    public List<MediaFile> fileList;

    public Map<String, Long> fileLocation;

    public String userDescription;

    public Double rating;
}
