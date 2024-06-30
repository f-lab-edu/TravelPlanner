package com.example.travelDiary.application.service.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import com.example.travelDiary.repository.persistence.review.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MediaFileAccessService {
    private final S3Service s3Service;
    private final ConversionService conversionService;
    private final MediaFileRepository mediaFileRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_EXPIRATION_TIME = 30; // cache expiration time in minutes

    @Autowired
    public MediaFileAccessService(S3Service s3Service, ConversionService conversionService, MediaFileRepository mediaFileRepository, RedisTemplate<String, Object> redisTemplate) {
        this.s3Service = s3Service;
        this.conversionService = conversionService;
        this.mediaFileRepository = mediaFileRepository;
        this.redisTemplate = redisTemplate;
    }

    //make idempotent for same file...
    //check for partial update/ failed update.
    public String saveMediaFile(PreSignedUploadInitiateRequest request) {
        Optional<MediaFile> existingMediaFile = mediaFileRepository
                .findByOriginalFileNameAndContentType(request.getOriginalFileName(), guessContentTypeFromName(request));

        MediaFile mediaFile = conversionService.convert(request, MediaFile.class);
        assert mediaFile != null;
        String key;
        // Update existing media file
        if (existingMediaFile.isPresent()) {
            key = existingMediaFile.get().getS3Key();
        } else {
            UUID mediaFileId = UUID.randomUUID();
            key = generateKey(request, mediaFileId);
        }
        mediaFile.setS3Key(key);

        // Cache media file in Redis with expiration
        redisTemplate.opsForValue().set(key, mediaFile, Duration.ofMinutes(CACHE_EXPIRATION_TIME));
        return key;

    }

    private String guessContentTypeFromName(PreSignedUploadInitiateRequest request) {
        return URLConnection.guessContentTypeFromName(request.getOriginalFileName());
    }

    private String generateKey(PreSignedUploadInitiateRequest request, UUID mediaFileId) {
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

    public URL getMediaFile(String s3Key) {
        return s3Service.createPresignedUrlForGet(s3Key);
    }

    public URL uploadMediaFile(PreSignedUploadInitiateRequest request) {
        String key = saveMediaFile(request);
//        log.info("on upload media file {}", mediaFileRepository.findByS3Key(key));
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
        redisTemplate.delete(request.getObjectKey());
        return "aborted";
    }

    public void markMediaUploadFinished(String objectKey) {
        log.info("mark media upload finished {}", objectKey);
        MediaFile mediaFile = (MediaFile) redisTemplate.opsForValue().get(objectKey);

        if (mediaFile == null) {
            mediaFile = mediaFileRepository.findByS3Key(objectKey);
            if (mediaFile == null) {
                s3Service.deleteS3Object(objectKey);
                return;
            }
        } else {
            redisTemplate.delete(objectKey);
        }

        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
        mediaFileRepository.save(mediaFile);
    }


    public MediaFileStatus getUploadStatus(String key) {
        MediaFile mediaFile = (MediaFile) redisTemplate.opsForValue().get(key);
        if (mediaFile != null) {
            return mediaFile.getMediaFileStatus();
        }

        mediaFile = mediaFileRepository.findByS3Key(key);
        if (mediaFile != null) {
            return mediaFile.getMediaFileStatus();
        }

        return MediaFileStatus.FAILED;
    }


    public void deleteMediaMetadata(String objectKey) {
        log.info("mark media delete finished {}", objectKey);
        mediaFileRepository.deleteByS3Key(objectKey);
    }
}
