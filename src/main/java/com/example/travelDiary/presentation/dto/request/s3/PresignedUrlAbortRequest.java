package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

@Data
public class PresignedUrlAbortRequest {
    public String uploadId;
}
