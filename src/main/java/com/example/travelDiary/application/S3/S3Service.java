package com.example.travelDiary.application.S3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.*;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUrlCreateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class S3Service {
    private final S3Client amazonS3Client;
    private final String bucketName;

    public S3Service(S3Client amazonS3Client, @Value("${aws.s3.profile.bucket}") String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    public 햣  getInitiateMultipartUploadResult(ObjectMetadata objectMetadata) {
        CreateMultipartUploadRequest uploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key("objectName")
                .contentType()
                .
                .build();
                bucketName, "objectName", objectMetadata);
        return amazonS3Client.initiateMultipartUpload(uploadRequest);
    }

    public URL getUrl(PreSignedUrlCreateRequest request, Date expirationDate) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, "objectName")
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expirationDate);

        generatePresignedUrlRequest.addRequestParameter("uploadId", request.getUploadId());
        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(request.getPartNumber()));

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    public CompleteMultipartUploadResult getCompleteMultipartUploadResult(FinishUploadRequest finishUploadRequest) {
        List<PartETag> partETags = new ArrayList<>();
        for (FinishUploadRequest.Part part : finishUploadRequest.getParts()) {
            partETags.add(new PartETag(part.getPartNumber(), part.getETag()));
        }

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(
                        bucketName,
                        "objectName",
                        finishUploadRequest.getUploadId(),
                        partETags);

        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public void abortUpload(PresignedUrlAbortRequest request) {
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(bucketName, "objectName", request.getUploadId());

        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
    }


}
