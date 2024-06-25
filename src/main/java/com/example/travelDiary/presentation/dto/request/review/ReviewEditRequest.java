package com.example.travelDiary.presentation.dto.request.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReviewEditRequest {

    public UUID reviewId;

    public List<MediaFile> fileList;

    public List<Long> fileLocation;

    public String userDescription;

    public Double rating;
}
