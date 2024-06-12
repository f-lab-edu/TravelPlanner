package com.example.travelDiary.domain.model.review;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID scheduleId;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<MediaFile> fileList;

    public String userDescription;

    public Double rating;

    @ElementCollection
    public List<Long> contentLocation;
}
