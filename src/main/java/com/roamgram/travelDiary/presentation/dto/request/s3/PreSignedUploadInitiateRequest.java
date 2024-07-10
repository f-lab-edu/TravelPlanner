package com.roamgram.travelDiary.presentation.dto.request.s3;

import lombok.Data;

import java.util.UUID;

@Data
public class PreSignedUploadInitiateRequest {

    private UUID scheduleId;
    public Long fileSize;
    public String originalFileName;
    public Long contentLocation;
}


//https://techblog.woowahan.com/11392/