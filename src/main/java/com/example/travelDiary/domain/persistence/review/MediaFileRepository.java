package com.example.travelDiary.domain.persistence.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
}
