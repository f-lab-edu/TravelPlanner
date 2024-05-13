package com.example.travelDiary.application.S3;

import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUrlCreateRequest;
import io.awspring.cloud.s3.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/S3Test")
public class S3Controller {
    private final S3TestService s3Service;

    @Autowired
    public S3Controller(S3TestService s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/test")
    public String s3Test(@RequestParam(value="path") String path) {
        return s3Service.createPresignedURLForUpload(path);
    }

    @PostMapping("/initiate-upload")
    public InitiateMultipartUploadResult initiateUpload(@RequestBody PreSignedUploadInitiateRequest request) {
        ObjectMetadata objectMetadata = ObjectMetadata
                .builder()
                .contentType(URLConnection.guessContentTypeFromName(request.getFileType()))
                .contentLength(request.getFileSize())
                .build();

    }

    @PostMapping("/presigned-url")
    public URL initiateUpload(@RequestBody PreSignedUrlCreateRequest request) {
        LocalDateTime expirationTime = LocalDateTime.from(
                LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant()
        );

    }
}
