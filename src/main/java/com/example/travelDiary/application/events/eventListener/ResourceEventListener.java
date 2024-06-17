package com.example.travelDiary.application.events.eventListener;

import com.example.travelDiary.application.events.permission.ResourceCreationEvent;
import com.example.travelDiary.application.events.permission.ResourceDeletionEvent;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResourceEventListener {

    private final ResourceService resourceService;
    private final AuthUserService authUserService;

    @Autowired
    public ResourceEventListener(ResourceService resourceService, AuthUserService authUserService) {
        this.resourceService = resourceService;
        this.authUserService = authUserService;
    }

    @EventListener
    public void handleResourceCreation( ResourceCreationEvent event ) {
        resourceService.linkResource(event.getResource(), event.getVisibility(), authUserService.getCurrentAuthenticatedUser());
    }

    @EventListener
    public void handleResourceDeletionEvent(ResourceDeletionEvent event) {
        resourceService.deleteAllById(event.getResourceIds());
    }
}
