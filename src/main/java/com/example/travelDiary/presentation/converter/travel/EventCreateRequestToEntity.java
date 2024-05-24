package com.example.travelDiary.presentation.converter.travel;

import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.presentation.dto.request.travel.event.EventMetaDataUpsertRequest;
import org.springframework.core.convert.converter.Converter;

public class EventCreateRequestToEntity implements Converter<EventMetaDataUpsertRequest, Event> {
    @Override
    public Event convert(EventMetaDataUpsertRequest source) {
        Event event = new Event();

        if(source.getEventEndTime() != null) {
            event.setEventEndTime(source.getEventEndTime());
        }
        if(source.getScheduleId() != null) {
            event.setScheduleId(source.getScheduleId());
        }
        if(source.getEventEndTime() != null) {
            event.setEventEndTime(source.getEventEndTime());
        }

        return event;
    }
}
