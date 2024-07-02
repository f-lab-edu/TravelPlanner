package com.example.travelDiary.application.service.travel.event;

import com.example.travelDiary.application.service.tags.TagsAccessService;
import com.example.travelDiary.application.service.wallet.MonetaryDomainQueryService;
import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.example.travelDiary.repository.persistence.travel.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityAccessService {
    private final ActivityRepository activityRepository;
    private final TagsAccessService tagsAccessService;
    private final MonetaryDomainQueryService monetaryDomainQueryService;

    @Autowired
    public ActivityAccessService(ActivityRepository activityRepository, TagsAccessService tagsAccessService, MonetaryDomainQueryService monetaryDomainQueryService) {
        this.activityRepository = activityRepository;
        this.tagsAccessService = tagsAccessService;
        this.monetaryDomainQueryService = monetaryDomainQueryService;
    }

    public Activity getEventById(UUID id) {
        return activityRepository.findById(id).orElseThrow();
    }

    @Transactional
    public List<MonetaryEvent> getAllMonetaryEvents(UUID eventId) {
        Activity activity = activityRepository.findById(eventId).orElseThrow();
        return monetaryDomainQueryService.convertAllToAggregates(activity.getMonetaryEvents());
    }

    @Transactional
    public List<MonetaryEvent> getAllMonetaryEventsInEventsByTag(UUID eventId, List<String> tagNames) {
        Activity activity = activityRepository.findById(eventId).orElseThrow();
        List<UUID> monetaryEventsId = activity.getMonetaryEvents().stream().map(MonetaryEventEntity::getId).toList();
        List<UUID> filteredMonetaryEventsId = tagsAccessService
                .filterEntitiesByTags(monetaryEventsId,tagNames);
        return monetaryDomainQueryService.findAllById(filteredMonetaryEventsId);
    }
}
