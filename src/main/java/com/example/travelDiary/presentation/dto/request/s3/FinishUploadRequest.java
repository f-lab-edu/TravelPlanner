package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FinishUploadRequest {
    @Data
    public static class PartData {
        private int partNumber;
        private String eTag;
    }

    private String uploadId;
    private String objectKey;
    private List<PartData> partData;
}

