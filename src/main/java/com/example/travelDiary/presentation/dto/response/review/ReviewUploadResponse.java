package com.example.travelDiary.presentation.dto.response.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.Review;
import lombok.Data;

import java.util.List;

@Data
public class ReviewUploadResponse {
    private Review review;
    private List<MediaFile> pendingOrFailedFiles;
}
