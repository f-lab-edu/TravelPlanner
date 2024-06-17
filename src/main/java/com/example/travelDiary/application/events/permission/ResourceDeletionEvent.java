package com.example.travelDiary.application.events.permission;

import lombok.Data;

import java.util.UUID;

@Data
public class ResourceDeletionEvent {
    private final UUID resourceId;
}
