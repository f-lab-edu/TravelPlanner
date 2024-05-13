package com.example.travelDiary.application.S3;

import com.example.travelDiary.presentation.dto.request.s3.PreSignedUrlCreateRequest;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3TestService {

    private final String bucket;
    private final S3Presigner s3Presigner;
    private final S3Template s3Template;

    private final S3Client s3Client;

    @Autowired
    public S3TestService(@Value("${aws.s3.bucket}") String bucket, S3Presigner s3Presigner, S3Template s3Template, S3Client s3Client) {
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
        this.s3Template = s3Template;
        this.s3Client = s3Client;
    }

    public String createPresignedURLForUpload(String path) {

        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(path)
                .contentType("image/jpeg")
                .build();

        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // The URL will expire in 5 minutes.
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }

    public CreateMultipartUploadResponse createMultipartUploadRequest (ObjectMetadata objectMetadata) {
        CreateMultipartUploadRequest uploadRequest = CreateMultipartUploadRequest
                .builder()
                .bucket("bucket")
                .key("key")
                .build();
        return s3Client.createMultipartUpload(uploadRequest);
    }
}
