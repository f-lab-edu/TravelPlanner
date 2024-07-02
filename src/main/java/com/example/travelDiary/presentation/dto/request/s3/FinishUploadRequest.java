package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

import java.util.List;

@Data
public class FinishUploadRequest {
    @Data
    public static class PartData {
        private int partNumber;
        private String eTag;
        private Long partSize;
    }

    private String uploadId;
    private String objectKey;
    private List<PartData> partData;
    private Long fileSize;
}

