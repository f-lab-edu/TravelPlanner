package com.example.travelDiary.presentation.dto.request.s3;

import lombok.Data;

import java.util.List;

@Data
public class FinishUploadRequest {
    @Data
    public static class Part {
        private int partNumber;
        private String eTag;
    }


    private String uploadId;
    private List<Part> parts;
}

