package com.roamgram.travelDiary.application.S3;

import com.roamgram.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.roamgram.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
public class S3Service {
    private final S3Client amazonS3Client;
    private final String bucketName;
    private final S3Presigner s3Presigner;

    @Autowired
    public S3Service(S3Client amazonS3Client, @Value("${aws.s3.profile.bucket}") String bucketName, S3Presigner s3Presigner) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
        this.s3Presigner = s3Presigner;
    }

    public URL createPresignedUrlForGet(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObject = s3Presigner.presignGetObject(getObjectPresignRequest);


        return presignedGetObject.url();
    }

    public URL createPresignedUrlForPut(String objectKey, String contentType, Long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(30))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

        return presignedPutObjectRequest.url();
    }



    public DeleteObjectResponse deleteS3Object(String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return amazonS3Client.deleteObject(deleteObjectRequest);
    }

    public URL createMultipartUploadRequest(String objectKey, String contentType) {
        CreateMultipartUploadRequest multiPartObjectRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        CreateMultipartUploadPresignRequest createMultipartUploadPresignRequest = CreateMultipartUploadPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .createMultipartUploadRequest(multiPartObjectRequest)
                .build();

        PresignedCreateMultipartUploadRequest presignedCreateMultipartUploadRequest = s3Presigner.presignCreateMultipartUpload(createMultipartUploadPresignRequest);

        return presignedCreateMultipartUploadRequest.url();
    }

    public URL generatePresignedUrlForPart(String objectKey, String uploadId, int partNumber) {
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .uploadPartRequest(uploadPartRequest)
                .build();

        PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(uploadPartPresignRequest);

        return presignedUploadPartRequest.url();
    }

    public CompleteMultipartUploadResponse completeMultipartUpload(String objectKey, FinishUploadRequest finishUploadRequest) {
        List<CompletedPart> completedParts = finishUploadRequest.getPartData().stream()
                .map(partData -> CompletedPart.builder()
                        .partNumber(partData.getPartNumber())
                        .eTag(partData.getETag())
                        .build())
                .toList();

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .multipartUpload(completedMultipartUpload)
                .uploadId(finishUploadRequest.getUploadId())
                .build();

        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public void abortUpload(String objectKey, PresignedUrlAbortRequest request) {
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                AbortMultipartUploadRequest
                        .builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .uploadId(request.getUploadId())
                        .build();

        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
    }

}
