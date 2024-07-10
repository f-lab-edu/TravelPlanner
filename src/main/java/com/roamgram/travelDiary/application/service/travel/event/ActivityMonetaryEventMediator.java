package com.roamgram.travelDiary.application.service.travel.event;

import com.roamgram.travelDiary.application.service.wallet.MonetaryDomainMutationService;
import com.roamgram.travelDiary.domain.model.travel.Activity;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.roamgram.travelDiary.repository.persistence.travel.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityMonetaryEventMediator {
    private final ActivityRepository activityRepository;
    private final MonetaryDomainMutationService monetaryDomainMutationService;

    @Autowired
    public ActivityMonetaryEventMediator(ActivityRepository activityRepository, MonetaryDomainMutationService monetaryDomainMutationService) {
        this.activityRepository = activityRepository;
        this.monetaryDomainMutationService = monetaryDomainMutationService;
    }

    @Transactional
    public Activity addMonetaryEvent(UUID eventId, MonetaryEvent monetaryEvent) {
        Activity activity = activityRepository.findById(eventId).orElseThrow();
        List<MonetaryEventEntity> savedMonetaryEvent = monetaryDomainMutationService.save(monetaryEvent);
        List<MonetaryEventEntity> associatedMonetaryEvents = activity.getMonetaryEvents();
        associatedMonetaryEvents.addAll(savedMonetaryEvent);
        return activityRepository.save(activity);
    }

    @Transactional
    public UUID deleteMonetaryEvent(UUID eventId, UUID monetaryEventId) {
        Activity activity = activityRepository.findById(eventId).orElseThrow();
        monetaryDomainMutationService.delete(String.valueOf(monetaryEventId));
        return monetaryEventId;
    }

    @Transactional
    public Activity updateMonetaryEvent(UUID eventId, UUID monetaryEventId, MonetaryEvent monetaryEvent) {
        Activity activity = activityRepository.findById(eventId).orElseThrow();
        monetaryDomainMutationService.update(String.valueOf(monetaryEventId), monetaryEvent);
        return activityRepository.save(activity);
    }


}
