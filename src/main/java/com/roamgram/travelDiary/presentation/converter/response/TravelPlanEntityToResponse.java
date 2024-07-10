package com.roamgram.travelDiary.presentation.converter.response;

import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.presentation.dto.response.travel.ScheduleResponse;
import com.roamgram.travelDiary.presentation.dto.response.travel.TravelPlanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TravelPlanEntityToResponse implements Converter<TravelPlan, TravelPlanResponse> {

    private ConversionService conversionService;

    @Autowired
    @Lazy
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public TravelPlanResponse convert(TravelPlan source) {
        if (source == null) {
            return null;
        }

        TravelPlanResponse dto = new TravelPlanResponse();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setTravelStartDate(source.getTravelStartDate());
        dto.setTravelEndDate(source.getTravelEndDate());
        dto.setPublic(source.isPublic());
        // Convert each Schedule entity to ScheduleResponse
        List<ScheduleResponse> scheduleResponses = source.getScheduleList().stream()
                .map(schedule -> conversionService.convert(schedule, ScheduleResponse.class))
                .collect(Collectors.toList());
        dto.setScheduleList(scheduleResponses); // Assuming Schedule is used as-is for now

        return dto;
    }
}
