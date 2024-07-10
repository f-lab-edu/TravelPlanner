package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.domain.model.review.MediaFile;
import com.roamgram.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.roamgram.travelDiary.application.service.review.MediaFileUtils.generateKey;
import static com.roamgram.travelDiary.application.service.review.MediaFileUtils.guessContentTypeFromName;

@Service
@Slf4j
public class MediaFileDataService {

    private final MediaFileService mediaFileService;
    private final MediaFileRedisService redisService;
    private final AuthUserService authUserService;
    private final ConversionService conversionService;

    public MediaFileDataService(MediaFileService mediaFileService,
                                MediaFileRedisService redisService, AuthUserService authUserService, ConversionService conversionService) {
        this.mediaFileService = mediaFileService;
        this.redisService = redisService;
        this.authUserService = authUserService;
        this.conversionService = conversionService;
    }

    @Transactional
    public String saveMediaFile(PreSignedUploadInitiateRequest request) {
        Optional<MediaFile> existingMediaFile = mediaFileService.findByOriginalFileNameAndContentType(request.getOriginalFileName(), guessContentTypeFromName(request));

        String contentType = guessContentTypeFromName(request);
        UUID userProfileId = authUserService.getCurrentUser().getId();
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

    @Transactional
    public void deleteMediaMetadata(String objectKey) {
        log.info("mark media delete finished {}", objectKey);
        mediaFileService.deleteByS3Key(objectKey);
    }

}
