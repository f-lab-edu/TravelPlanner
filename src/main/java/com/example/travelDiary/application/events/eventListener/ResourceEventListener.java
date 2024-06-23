package com.example.travelDiary.application.events.eventListener;

import com.example.travelDiary.application.events.resource.ResourceDeletionEvent;
import com.example.travelDiary.common.permissions.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ResourceEventListener {

    private final ResourceService resourceService;

    @Autowired
    public ResourceEventListener(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Transactional
    @EventListener
    public void handleResourceDeletionEvent(ResourceDeletionEvent event) {
        resourceService.delinkPermissions(event.getResourceIds());
    }

}
