package com.example.travelDiary.repository.persistence.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
    MediaFile findByS3Key(String objectKey);

    void deleteByS3Key(String objectKey);
}
