package com.example.travelDiary.application.events.permission;

import com.example.travelDiary.domain.IdentifiableResource;
import lombok.Data;

@Data
public class ResourceCreationEvent {
    private final IdentifiableResource resource;
    private final String visibility;
}
