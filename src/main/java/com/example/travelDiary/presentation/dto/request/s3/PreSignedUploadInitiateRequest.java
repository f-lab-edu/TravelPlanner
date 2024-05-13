package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

@Data
public class PreSignedUploadInitiateRequest {
    public Long fileSize;

    public String fileType;

    public String originalFileName;
}


//https://techblog.woowahan.com/11392/