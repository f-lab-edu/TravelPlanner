package com.example.travelDiary.domain.model.tags;

import com.example.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tag_association")
@Data
public class TagAssociation implements IdentifiableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID associationId;

    @ManyToOne
    private Tags tag;

    private String entityType;
    private UUID entityId;
    private LocalDateTime timestamp;

    @Override
    public UUID getId() {
        return associationId;
    }

    // Getters and setters
}