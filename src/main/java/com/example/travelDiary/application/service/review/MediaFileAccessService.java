package com.example.travelDiary.application.service.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MediaFileAccessService {
    private final S3Service s3Service;
    private final ConversionService conversionService;
    private final MediaFileService mediaFileService;
    private final MediaFileRedisService redisService;
    private final AuthUserServiceImpl authUserServiceImpl;

    @Autowired
    public MediaFileAccessService(S3Service s3Service,
                                  ConversionService conversionService,
                                  MediaFileService mediaFileService,
                                  MediaFileRedisService redisService,
                                  AuthUserServiceImpl authUserServiceImpl) {
        this.s3Service = s3Service;
        this.conversionService = conversionService;
        this.mediaFileService = mediaFileService;
        this.redisService = redisService;
        this.authUserServiceImpl = authUserServiceImpl;
    }

    @Transactional
    public String saveMediaFile(PreSignedUploadInitiateRequest request) {
        Optional<MediaFile> existingMediaFile = mediaFileService.findByOriginalFileNameAndContentType(request.getOriginalFileName(), guessContentTypeFromName(request));

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
        redisService.cacheMediaFile(key, mediaFile);
        return key;
    }

    public URL getMediaFile(String s3Key) {
        return s3Service.createPresignedUrlForGet(s3Key);
    }

    public URL uploadMediaFile(PreSignedUploadInitiateRequest request) {
        String key = saveMediaFile(request);
        log.info("media file key on upload {}", key);
        return s3Service.createPresignedUrlForPut(key, guessContentTypeFromName(request), request.getFileSize());
    }

    public DeleteObjectResponse deleteMediaFile(String objectKey) {
        DeleteObjectResponse deleteObjectResponse = s3Service.deleteS3Object(objectKey);
        deleteMediaMetadata(objectKey);
        return deleteObjectResponse;
    }

    public URL uploadMediaFileMultipart(PreSignedUploadInitiateRequest request) {
        String key = saveMediaFile(request);
        return s3Service.createMultipartUploadRequest(key, guessContentTypeFromName(request));
    }

    public String uploadMediaFileMultipartComplete(FinishUploadRequest finishUploadRequest) {
        s3Service.completeMultipartUpload(finishUploadRequest.getObjectKey(), finishUploadRequest);
        markMediaUploadFinished(finishUploadRequest.getObjectKey());
        return "completed";
    }

    public String abortUpload(PresignedUrlAbortRequest request) {
        s3Service.abortUpload(request.getObjectKey(), request);
        redisService.deleteMediaFile(request.getObjectKey());
        return "aborted";
    }

    public MediaFile reconstructObjectFromKey(String objectKey) {
        String[] parts = objectKey.split("/");
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid key format");
        }
        String fileSize = parts[parts.length - 3];
        String contentType = parts[parts.length - 4];
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
        MediaFile mediaFile = redisService.getMediaFile(objectKey);

        if (mediaFile == null) {
            mediaFile = mediaFileService.findByS3Key(objectKey);
            if (mediaFile == null) {
                MediaFile reconstructed = reconstructObjectFromKey(objectKey);
                log.info("reconstructed mediaFile from ObjectKey {} giving entity {}", objectKey, reconstructed);
                mediaFileService.saveMediaFile(reconstructed);
                return;
            }
        } else {
            redisService.deleteMediaFile(objectKey);
        }

        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
        mediaFileService.saveMediaFile(mediaFile);
    }

    public MediaFileStatus getUploadStatus(String key) {
        MediaFile mediaFile = redisService.getMediaFile(key);
        if (mediaFile != null) {
            return mediaFile.getMediaFileStatus();
        }

        mediaFile = mediaFileService.findByS3Key(key);
        if (mediaFile != null) {
            return mediaFile.getMediaFileStatus();
        }

        return MediaFileStatus.FAILED;
    }

    @Transactional
    public void deleteMediaMetadata(String objectKey) {
        log.info("mark media delete finished {}", objectKey);
        mediaFileService.deleteByS3Key(objectKey);
    }

    private String guessContentTypeFromName(PreSignedUploadInitiateRequest request) {
        return URLConnection.guessContentTypeFromName(request.getOriginalFileName());
    }

    private static String generateMD5Hash(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, bytes);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String generateKey(PreSignedUploadInitiateRequest request, String contentType, UUID userProfileId) {
        String sanitizedFileName = request
                .getOriginalFileName()
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .toLowerCase();

        String keyfront = String.format("uploads/%s/%s/%s/%s/%s",
                userProfileId,
                sanitizedFileName,
                contentType,
                request.getFileSize(),
                request.getScheduleId()
        );
        return keyfront + "/" + generateMD5Hash(keyfront);
    }
}
