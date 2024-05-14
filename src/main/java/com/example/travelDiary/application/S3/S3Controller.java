package com.example.travelDiary.application.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUrlCreateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/S3Test")
public class S3Controller {

    private final AmazonS3 amazonS3Client;
    private final S3Service s3Service;

    public S3Controller(AmazonS3 amazonS3Client, S3Service s3Service) {
        this.amazonS3Client = amazonS3Client;
        this.s3Service = s3Service;
    }

    @PostMapping("/initiate-upload")
    public InitiateMultipartUploadResult initiateUpload(
            @RequestBody PreSignedUploadInitiateRequest request) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getFileSize());
        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(request.getFileType()));
        return s3Service.getInitiateMultipartUploadResult(objectMetadata);
    }

    @PostMapping("/presigned-url")
    public URL initiateUpload(@RequestBody PreSignedUrlCreateRequest request) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        return s3Service.getUrl(request, expirationDate);
    }

    @PostMapping("/complete-upload")
    public CompleteMultipartUploadResult completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
        return s3Service.getCompleteMultipartUploadResult(finishUploadRequest);
    }

    @PostMapping("/abort-upload")
    public void abortUpload (@RequestBody PresignedUrlAbortRequest request) {
        s3Service.abortUpload(request);
    }

}
