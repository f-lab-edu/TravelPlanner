package com.roamgram.travelDiary.application.events.eventListener;

import com.roamgram.travelDiary.application.events.travelplan.TravelPlanDeletionEvent;
import com.roamgram.travelDiary.common.permissions.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class TravelPlanEventListener {

    private final ResourceService resourceService;

    @Autowired
    public TravelPlanEventListener(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Transactional
    @EventListener
    public void handleResourceDeletionEvent(TravelPlanDeletionEvent event) {
        resourceService.delinkPermissions(event.getResourceIds());
    }

}
