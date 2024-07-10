package com.roamgram.travelDiary.presentation.converter.request.travel;

import com.roamgram.travelDiary.domain.model.travel.Activity;
import com.roamgram.travelDiary.presentation.dto.request.travel.event.ActivityMetaDataUpsertRequest;
import org.springframework.core.convert.converter.Converter;

public class ActivityCreateRequestToEntity implements Converter<ActivityMetaDataUpsertRequest, Activity> {
    @Override
    public Activity convert(ActivityMetaDataUpsertRequest source) {
        Activity activity = new Activity();

        if(source.getEventEndTime() != null) {
            activity.setEventEndTime(source.getEventEndTime());
        }
        if(source.getScheduleId() != null) {
            activity.setScheduleId(source.getScheduleId());
        }
        if(source.getEventEndTime() != null) {
            activity.setEventEndTime(source.getEventEndTime());
        }
        if(source.getName() != null) {
            activity.setName(source.getName());
        }
        if(source.getDescription() != null) {
            activity.setDescription(source.getDescription());
        }

        return activity;
    }
}
