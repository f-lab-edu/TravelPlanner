package com.example.travelDiary.presentation.dto.request.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;

@Data
public class ReviewUploadRequest {

    public List<MediaFile> fileList;

    public List<Long> fileLocation;

    public String userDescription;

    public Double rating;

}
