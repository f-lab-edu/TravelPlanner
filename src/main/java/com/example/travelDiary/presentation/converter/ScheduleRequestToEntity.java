package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.ScheduleUpsertRequest;
import org.springframework.core.convert.converter.Converter;

public class ScheduleRequestToEntity implements Converter<ScheduleUpsertRequest, Schedule> {
    @Override
    public Schedule convert(ScheduleUpsertRequest source) {
        Schedule entity = new Schedule();

        if (source.getPlace() != null) {
            entity.setPlace(source.getPlace());
        }
        if (source.getReview() != null) {
            entity.setReview(source.getReview());
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
