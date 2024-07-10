package com.roamgram.travelDiary.presentation.dto.request.s3;

import lombok.Data;

@Data
public class PreSignedUrlCreateRequest {
    public String uploadId;
    public Integer partNumber;
}
