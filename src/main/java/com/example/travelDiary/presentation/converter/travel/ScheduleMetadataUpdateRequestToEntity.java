package com.example.travelDiary.presentation.converter.travel;

import com.example.travelDiary.domain.model.travel.schedule.Schedule;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import org.springframework.core.convert.converter.Converter;

public class ScheduleMetadataUpdateRequestToEntity implements Converter<ScheduleMetadataUpdateRequest, Schedule> {

    @Override
    public Schedule convert(ScheduleMetadataUpdateRequest source) {
        Schedule schedule = new Schedule();
        if (source.getScheduleId() != null) {
            schedule.setId(source.getScheduleId());
        }

        if (source.getIsActuallyVisited() != null) {
            schedule.setIsActuallyVisited(source.getIsActuallyVisited());
        }

        if (source.getTravelDate() != null) {
            schedule.setTravelDate(source.getTravelDate());
        }

        if (source.getOrderOfTravel() != null) {
            schedule.setOrderOfTravel(source.getOrderOfTravel());
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
