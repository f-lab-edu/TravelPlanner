package com.roamgram.travelDiary.presentation.dto.response.review;

import com.roamgram.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;

@Data
public class ReviewUploadResponse {
    private ReviewResponse review;
    private List<MediaFile> pendingOrFailedFiles;
}
