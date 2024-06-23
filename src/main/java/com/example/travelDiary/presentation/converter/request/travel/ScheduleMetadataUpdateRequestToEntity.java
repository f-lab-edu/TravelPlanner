package com.example.travelDiary.presentation.converter.request.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import org.springframework.core.convert.converter.Converter;

public class ScheduleMetadataUpdateRequestToEntity implements Converter<ScheduleMetadataUpdateRequest, Schedule> {

    @Override
    public Schedule convert(ScheduleMetadataUpdateRequest source) {
        Schedule schedule = new Schedule();
        if (source.getScheduleId() != null) {
            schedule.setId(source.getScheduleId());
        }

        if (source.getName() != null) {
            schedule.setName(source.getName());
        }

        if (source.getIsActuallyVisited() != null) {
            schedule.setIsActuallyVisited(source.getIsActuallyVisited());
        }

        if(source.getName() != null) {
            schedule.setName(source.getName());
        }
        if(source.getDescription() != null) {
            schedule.setDescription(source.getDescription());
        }

        if (source.getTravelStartTimeEstimate() != null) {
            schedule.setTravelStartTimeEstimate(source.getTravelStartTimeEstimate());
        }

        if (source.getTravelDepartTimeEstimate() != null) {
            schedule.setTravelDepartTimeEstimate(source.getTravelDepartTimeEstimate());
        }

        return schedule;
    }
}
