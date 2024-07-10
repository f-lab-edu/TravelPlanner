package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.domain.model.review.MediaFile;
import com.roamgram.travelDiary.repository.persistence.review.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class MediaFileService {
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public MediaFileService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public Optional<MediaFile> findByOriginalFileNameAndContentType(String originalFileName, String contentType) {
        return mediaFileRepository.findByOriginalFileNameAndContentType(originalFileName, contentType);
    }

    @Transactional
    public void saveMediaFile(MediaFile mediaFile) {
        mediaFileRepository.save(mediaFile);
    }

    @Transactional
    public MediaFile findByS3Key(String s3Key) {
        return mediaFileRepository.findByS3Key(s3Key);
    }

    @Transactional
    public void deleteByS3Key(String s3Key) {
        mediaFileRepository.deleteByS3Key(s3Key);
    }
}
