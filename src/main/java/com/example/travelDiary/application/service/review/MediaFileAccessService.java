package com.example.travelDiary.application.service.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
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

import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MediaFileAccessService {
    private final S3Service s3Service;
    private final ConversionService conversionService;
    private final MediaFileRepository mediaFileRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_EXPIRATION_TIME = 40; // cache expiration time in minutes
    private final AuthUserServiceImpl authUserServiceImpl;

    @Autowired
    public MediaFileAccessService(S3Service s3Service, ConversionService conversionService, MediaFileRepository mediaFileRepository, RedisTemplate<String, Object> redisTemplate, AuthUserServiceImpl authUserServiceImpl) {
        this.s3Service = s3Service;
        this.conversionService = conversionService;
        this.mediaFileRepository = mediaFileRepository;
        this.redisTemplate = redisTemplate;
        this.authUserServiceImpl = authUserServiceImpl;
    }

    //make idempotent for same file...
    //check for partial update/ failed update.
    // -- find ways to reconstruct. / recover from s3.
    public String saveMediaFile(PreSignedUploadInitiateRequest request) {
        Optional<MediaFile> existingMediaFile = mediaFileRepository
                .findByOriginalFileNameAndContentType(request.getOriginalFileName(), guessContentTypeFromName(request));

        String contentType = guessContentTypeFromName(request);
        UUID userProfileId = authUserServiceImpl.getCurrentUser().getId();
        MediaFile mediaFile = conversionService.convert(request, MediaFile.class);
        assert mediaFile != null;
        String key;
        // Update existing media file
        if (existingMediaFile.isPresent()) {
            key = existingMediaFile.get().getS3Key();
        } else {
            key = generateKey(request, contentType, userProfileId);
        }
        mediaFile.setS3Key(key);
        mediaFile.setContentType(contentType);
        // Cache media file in Redis with expiration
        redisTemplate.opsForValue().set(key, mediaFile, Duration.ofMinutes(CACHE_EXPIRATION_TIME));
        return key;

    }

    private String guessContentTypeFromName(PreSignedUploadInitiateRequest request) {
        return URLConnection.guessContentTypeFromName(request.getOriginalFileName());
    }

    private static String generateMD5Hash(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, bytes);
            return bigInt.toString(16); // Convert to hexadecimal representation
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String generateKey(PreSignedUploadInitiateRequest request, String contentType, UUID userProfileId) {
        // Sanitize original file name
        String sanitizedFileName = request
                .getOriginalFileName()
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .toLowerCase();

        // Example key structure
        String keyfront = String.format("uploads/%s/%s/%s/%s/%s",
                userProfileId,
                sanitizedFileName,
                contentType,
                request.getFileSize(),
                request.getScheduleId()
        );
        return keyfront + "/" + generateMD5Hash(keyfront);
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

    public MediaFile reconstructObjectFromKey(String objectKey) {
        // Split the key based on '/'
        String[] parts = objectKey.split("/");

        // Ensure the key follows the expected structure
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid key format");
        }

        // Third last part is the file size
        String fileSize = parts[parts.length - 3];

        // Second last part is the content type
        String contentType = parts[parts.length - 4];

        // Fourth last part is the sanitized file name
        String sanitizedFileName = parts[parts.length - 5];

        MediaFile mediaFile = MediaFile.builder()
                .mediaFileStatus(MediaFileStatus.UPLOADED)
                .originalFileName(sanitizedFileName)
                .sizeBytes(Long.valueOf(fileSize))
                .s3Key(objectKey)
                .contentType(contentType)
                .build();

        return mediaFile;
    }

    public void markMediaUploadFinished(String objectKey) {
        log.info("mark media upload finished {}", objectKey);
        MediaFile mediaFile = (MediaFile) redisTemplate.opsForValue().get(objectKey);

        if (mediaFile == null) {
            mediaFile = mediaFileRepository.findByS3Key(objectKey);
            if (mediaFile == null) {
                MediaFile reconstructed = reconstructObjectFromKey(objectKey);
                log.info("reconstructed mediaFile from ObjectKey {} giving entity {}", objectKey, reconstructed);
                mediaFileRepository.save(reconstructed);
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
