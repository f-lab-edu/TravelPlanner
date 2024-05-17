package com.example.travelDiary.domain.model.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID reviewId;

    public Long sizeBytes;

    public String contentType;

    public String originalFileName;

    public String s3Key;

    public Long contentLocation;

    public MediaFileStatus mediaFileStatus;

}