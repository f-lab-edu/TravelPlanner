package com.roamgram.travelDiary.presentation.converter.request.travel;

import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import org.springframework.core.convert.converter.Converter;

public class ScheduleInsertRequestToEntity implements Converter<ScheduleInsertRequest, Schedule> {
    @Override
    public Schedule convert(ScheduleInsertRequest source) {
        Schedule schedule = new Schedule();

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
