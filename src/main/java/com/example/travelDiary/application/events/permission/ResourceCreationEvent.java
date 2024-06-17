package com.example.travelDiary.application.events.permission;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ResourceCreationEvent {
    private final UUID uuid;
    private final String resourceType;
    private final String visibility;
    private final Instant creationTime = Instant.now();
}
