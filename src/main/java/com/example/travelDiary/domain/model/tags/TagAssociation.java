package com.example.travelDiary.domain.model.tags;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tag_association")
@Data
public class TagAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID associationId;

    @ManyToOne
    private Tags tag;

    private String entityType;
    private UUID entityId;
    private LocalDateTime timestamp;

    // Getters and setters
}