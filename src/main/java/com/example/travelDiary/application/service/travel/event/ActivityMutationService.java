package com.example.travelDiary.application.service.travel.event;

import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.presentation.dto.request.travel.event.ActivityMetaDataUpsertRequest;
import com.example.travelDiary.repository.persistence.travel.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActivityMutationService {
    private final ConversionService conversionService;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityMutationService(ConversionService conversionService, ActivityRepository activityRepository) {
        this.conversionService = conversionService;
        this.activityRepository = activityRepository;
    }

    public Activity createEvent(ActivityMetaDataUpsertRequest request) {
        Activity activity = conversionService.convert(request, Activity.class);
        assert activity != null;
        return activityRepository.save(activity);
    }

    public Activity updateEventMetaData(ActivityMetaDataUpsertRequest request) {
        Activity activity = activityRepository.findById(request.getId()).orElseThrow();
        activity.setEventEndTime(request.getEventEndTime());
        activity.setEventStartTime(request.getEventStartTime());
        return activityRepository.save(activity);
    }

    public UUID deleteEvent(UUID eventId) {
        activityRepository.deleteById(eventId);
        return eventId;
    }


}
