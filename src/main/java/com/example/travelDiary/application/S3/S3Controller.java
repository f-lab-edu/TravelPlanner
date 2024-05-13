package com.example.travelDiary.application.S3;

import com.amazonaws.HttpMethod;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/S3Test")
public class S3Controller {

    private final AmazonS3 amazonS3Client;

    public S3Controller(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    @PostMapping("/initiate-upload")
    public InitiateMultipartUploadResult initiateUpload(
            @RequestBody PreSignedUploadInitiateRequest request) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getFileSize());
        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(request.getFileType()));
        InitiateMultipartUploadRequest uploadRequest = new InitiateMultipartUploadRequest(
                "bucketName", "objectName", objectMetadata);
        return amazonS3Client.initiateMultipartUpload(uploadRequest);
    }
    @PostMapping("/presigned-url")
    public URL initiateUpload(@RequestBody PreSignedUrlCreateRequest request) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest("bucketName", "objectName")
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expirationDate);

        generatePresignedUrlRequest.addRequestParameter("uploadId", request.getUploadId());
        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(request.getPartNumber()));

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    @PostMapping("/complete-upload")
    public CompleteMultipartUploadResult completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
        List<PartETag> partETags = new ArrayList<>();
        for (FinishUploadRequest.Part part : finishUploadRequest.getParts()) {
            partETags.add(new PartETag(part.getPartNumber(), part.getETag()));
        }

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(
                        "bucketName",
                        "objectName",
                        finishUploadRequest.getUploadId(),
                        partETags);

        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    @PostMapping("/abort-upload")
    public void initiateUpload(@RequestBody PresignedUrlAbortRequest request) {
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest("bucketName", "objectName", request.getUploadId());

        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
    }
}
