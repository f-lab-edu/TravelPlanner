package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.presentation.dto.request.TravelPlanCreateRequestDTO;
import org.springframework.core.convert.converter.Converter;

public class TravelPlanRequestToEntity implements Converter<TravelPlanCreateRequestDTO, TravelPlan> {
    @Override
    public TravelPlan convert(TravelPlanCreateRequestDTO source) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.name = source.name;
        travelPlan.travelStartDate = source.startDate;
        travelPlan.travelEndDate = source.endDate;
        return travelPlan;
    }
}
