package com.example.travelDiary.presentation.converter.travel;

import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.presentation.dto.request.travel.event.ActivityMetaDataUpsertRequest;
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

        return activity;
    }
}
