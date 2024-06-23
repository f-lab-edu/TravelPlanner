package com.example.travelDiary.presentation.converter.response;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.response.travel.ScheduleResponse;
import org.springframework.core.convert.converter.Converter;

public class ScheduleEntityToResponse implements Converter<Schedule, ScheduleResponse> {
    @Override
    public ScheduleResponse convert(Schedule source) {
        if (source == null) {
            return null;
        }

        ScheduleResponse dto = new ScheduleResponse();
        dto.setId(source.getId());
        dto.setTravelPlanId(source.getTravelPlanId());
        dto.setName(source.getName());
        dto.setDescription(source.getDescription());
        dto.setPlace(source.getPlace()); // Assuming Place is used as-is for now
        dto.setReview(source.getReview()); // Assuming Review is used as-is for now
        dto.setIsActuallyVisited(source.getIsActuallyVisited());
        dto.setTravelStartTimeEstimate(source.getTravelStartTimeEstimate());
        dto.setTravelDepartTimeEstimate(source.getTravelDepartTimeEstimate());
        dto.setInwardRoute(source.getInwardRoute()); // Assuming Route is used as-is for now
        dto.setOutwardRoute(source.getOutwardRoute()); // Assuming Route is used as-is for now
        dto.setActivities(source.getActivities()); // Assuming Activity is used as-is for now

        return dto;
    }
}
