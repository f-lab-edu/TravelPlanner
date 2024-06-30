package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

import java.util.UUID;

@Data
public class PreSignedUploadInitiateRequest {
    public Long fileSize;

    public String originalFileName;

    private UUID reviewId;

    public Long contentLocation;
}


//https://techblog.woowahan.com/11392/