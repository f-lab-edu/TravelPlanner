package com.example.travelDiary.presentation.dto.response.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ReviewResponse {

    private UUID id;

    private UUID scheduleId;

    public List<MediaFile> fileList;

    public String userDescription;

    public Double rating;

    public Map<String,Long> contentLocation;
}
