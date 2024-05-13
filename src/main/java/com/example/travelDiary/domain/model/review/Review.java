package com.example.travelDiary.domain.model.review;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @OneToMany
//    public List<MediaFile> imageList;

    public String userDescription;

    public Double rating;


}
