package com.roamgram.travelDiary.presentation.converter.request.travel;

import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import org.springframework.core.convert.converter.Converter;

public class TravelPlanRequestToEntity implements Converter<TravelPlanUpsertRequestDTO, TravelPlan> {
    @Override
    public TravelPlan convert(TravelPlanUpsertRequestDTO source) {
        TravelPlan entity = new TravelPlan();
        if (source.getName() != null) {
            entity.setName(source.getName());
        }
        if (source.getStartDate() != null) {
            entity.setTravelStartDate(source.getStartDate());
        }
        if (source.getEndDate() != null) {
            entity.setTravelEndDate(source.getEndDate());
        }
        return entity;
    }
}
