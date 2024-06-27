package com.example.travelDiary.presentation.dto.request.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReviewUploadRequest {

    public List<MediaFile> fileList;

    public Map<String, Long> fileLocation;

    public String userDescription;

    public Double rating;

}
