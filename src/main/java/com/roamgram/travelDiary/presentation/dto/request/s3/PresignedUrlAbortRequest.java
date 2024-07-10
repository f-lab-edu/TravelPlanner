package com.roamgram.travelDiary.presentation.dto.request.s3;

import lombok.Data;

@Data
public class PresignedUrlAbortRequest {
    public String objectKey;
    public String uploadId;
}
