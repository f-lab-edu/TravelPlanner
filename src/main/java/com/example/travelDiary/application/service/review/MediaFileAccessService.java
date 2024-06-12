package com.example.travelDiary.application.service.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.repository.persistence.review.MediaFileRepository;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class MediaFileAccessService {
    private final S3Service s3Service;
    private final ConversionService conversionService;
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public MediaFileAccessService(S3Service s3Service, ConversionService conversionService, MediaFileRepository mediaFileRepository) {
        this.s3Service = s3Service;
        this.conversionService = conversionService;
        this.mediaFileRepository = mediaFileRepository;
    }

    private String saveMediaFile(PreSignedUploadInitiateRequest request) {
        MediaFile mediaFile = conversionService.convert(request, MediaFile.class);
        assert mediaFile != null;
        UUID mediaFileId = mediaFileRepository.save(mediaFile).getId();
        String key = generateKey(request, mediaFileId);
        mediaFile.setS3Key(key);
        mediaFileRepository.save(mediaFile);
        return key;
    }

    private String guessContentTypeFromName(PreSignedUploadInitiateRequest request) {
        return URLConnection.guessContentTypeFromName(request.getFileType());
    }

    private String generateKey(PreSignedUploadInitiateRequest request, UUID mediaFileId){
        String timestamp = Instant.now().toString();
        // Sanitize original file name
        String sanitizedFileName = request
                .getOriginalFileName()
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .toLowerCase();

        // Example key structure
        return String.format("uploads/%s/%s/%s/%s-%s",
                request.getUserId(),
                request.getReviewId(),
                mediaFileId,
                timestamp,
                sanitizedFileName);
    }

    public URL getMediaFile(String objectKey){
        return s3Service.createPresignedUrlForGet(objectKey);
    }

    public URL uploadMediaFile(PreSignedUploadInitiateRequest request) {
        String key = saveMediaFile(request);
        log.info("on upload media file {}", mediaFileRepository.findByS3Key(key));
        log.info("media file key on upload {}", key);

        return s3Service.createPresignedUrlForPut(key,
                guessContentTypeFromName(request),
                request.getFileSize());
    }

    public DeleteObjectResponse deleteMediaFile(String objectKey) {
        DeleteObjectResponse deleteObjectResponse = s3Service.deleteS3Object(objectKey);
        deleteMediaMetadata(objectKey);
        return deleteObjectResponse;
    }

    public URL uploadMediaFileMultipart(PreSignedUploadInitiateRequest request) {
        String key = saveMediaFile(request);

        return s3Service.createMultipartUploadRequest(
                key,
                guessContentTypeFromName(request)
        );
    }

    public String uploadMediaFileMultipartComplete(FinishUploadRequest finishUploadRequest) {
        s3Service.completeMultipartUpload(finishUploadRequest.getObjectKey(), finishUploadRequest);
        markMediaUploadFinished(finishUploadRequest.getObjectKey());
        return "completed";
    }

    public String abortUpload(PresignedUrlAbortRequest request) {
        s3Service.abortUpload(request.getObjectKey(), request);
        mediaFileRepository.deleteByS3Key(request.getObjectKey());
        return "aborted";
    }

    public void markMediaUploadFinished(String objectKey) {
        log.info("mark media upload finished {}", objectKey);
        MediaFile mediaFile = mediaFileRepository.findByS3Key(objectKey);
        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
        mediaFileRepository.save(mediaFile);
    }


    public void deleteMediaMetadata(String objectKey) {
        log.info("mark media delete finished {}", objectKey);
        mediaFileRepository.deleteByS3Key(objectKey);
    }
}
