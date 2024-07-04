package com.example.travelDiary.domain.model.review;

import com.example.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile implements IdentifiableResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID review;

    public Long sizeBytes;

    public String contentType;

    public String originalFileName;

    public String s3Key;

    public MediaFileStatus mediaFileStatus;

}