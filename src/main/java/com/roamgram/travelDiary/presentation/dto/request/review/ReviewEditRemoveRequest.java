package com.roamgram.travelDiary.presentation.dto.request.review;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReviewEditRemoveRequest {
    public UUID reviewId;
    public List<String> fileList;
}
