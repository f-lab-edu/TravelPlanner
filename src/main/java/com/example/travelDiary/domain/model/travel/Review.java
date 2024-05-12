package com.example.travelDiary.domain.model.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Review {
    @Id
    private Long id;

//    @OneToMany
//    public List<Image> imageList;

    public String userDescription;

    public Double rating;


}
