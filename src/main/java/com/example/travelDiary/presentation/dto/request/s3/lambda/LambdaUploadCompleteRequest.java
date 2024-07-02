package com.example.travelDiary.presentation.dto.request.s3.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LambdaUploadCompleteRequest {
    @JsonProperty("objectKey")
    public String objectKey;

    @JsonProperty("size")
    public String size;

}
