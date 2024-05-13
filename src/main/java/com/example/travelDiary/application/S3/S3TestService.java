package com.example.travelDiary.application.S3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3TestService {

    private final String bucket;
    private final S3Presigner s3Presigner;

    @Autowired
    public S3TestService(@Value("${aws.s3.bucket}") String bucket, S3Presigner s3Presigner) {
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
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
}
