package com.example.travelDiary.application.events.eventListener;

import com.example.travelDiary.application.events.permission.ResourceCreationEvent;
import com.example.travelDiary.application.events.permission.ResourceDeletionEvent;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResourceEventListener {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceEventListener(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @EventListener
    public void handleResourceCreationEvent(ResourceCreationEvent event) {
        Resource resource = Resource
                .builder()
                .createTime(event.getCreationTime())
                .type(event.getResourceType())
                .resourceUUID(event.getUuid())
                .visibility(event.getVisibility())
                .build();
        resourceRepository.save(resource);
    }

    @EventListener
    public void handleResourceDeletionEvent(ResourceDeletionEvent event) {
        resourceRepository.deleteById(event.getResourceId());
    }
}
