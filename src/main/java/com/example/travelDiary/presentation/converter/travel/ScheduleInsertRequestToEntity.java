package com.example.travelDiary.presentation.converter.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import org.springframework.core.convert.converter.Converter;

public class ScheduleInsertRequestToEntity implements Converter<ScheduleInsertRequest, Schedule> {
    @Override
    public Schedule convert(ScheduleInsertRequest source) {
        Schedule entity = new Schedule();

        if (source.getPlace() != null) {
            entity.setPlace(source.getPlace());
        }
        if (source.getIsActuallyVisited() != null) {
            entity.setIsActuallyVisited(source.getIsActuallyVisited());
        }
        if (source.getTravelDate() != null) {
            entity.setTravelDate(source.getTravelDate());
        }
        if (source.getOrderOfTravel() != null) {
            entity.setOrderOfTravel(source.getOrderOfTravel());
        }
        if (source.getTravelStartTimeEstimate() != null) {
            entity.setTravelStartTimeEstimate(source.getTravelStartTimeEstimate());
        }
        if (source.getTravelDepartTimeEstimate() != null) {
            entity.setTravelDepartTimeEstimate(source.getTravelDepartTimeEstimate());
        }
        return entity;
    }
}
