package com.example.travelDiary.domain.tags;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany
    public List<Category> category;


}
