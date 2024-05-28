package com.example.travelDiary.domain.model.tags;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Data
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    private String name;
    private String description;

    // Getters and setters
}
