package com.example.travelDiary.application.events.resource;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResourceDeletionEvent {
    private final List<UUID> resourceIds;
}