package com.example.travelDiary.domain.persistence.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class UserPost {
    @Id
    private Long id;

    @OneToOne
    public Schedule schedule;

    @OneToMany
    public List<Image> imageList;

    public String userDescription;

    public Double rating;


}
