package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.presentation.converter.request.location.PlaceUpdateRequestToEntity;
import com.example.travelDiary.presentation.converter.request.mediaFile.PreSignedUploadRequestToMediaFile;
import com.example.travelDiary.presentation.converter.request.review.ReviewUpsertRequestToReview;
import com.example.travelDiary.presentation.converter.request.travel.*;
import com.example.travelDiary.presentation.converter.request.wallet.CurrencyConversionRequestToAggregate;
import com.example.travelDiary.presentation.converter.request.wallet.ExpenditureRequestToAggregate;
import com.example.travelDiary.presentation.converter.request.wallet.IncomeRequestToAggregate;
import com.example.travelDiary.presentation.converter.response.ScheduleEntityToResponse;
import com.example.travelDiary.presentation.converter.response.TravelPlanEntityToResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class ConversionConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TravelPlanRequestToEntity());
        registry.addConverter(new ScheduleInsertRequestToEntity());
        registry.addConverter(new PreSignedUploadRequestToMediaFile());
        registry.addConverter(new ReviewUpsertRequestToReview());
        registry.addConverter(new ScheduleMetadataUpdateRequestToEntity());
        registry.addConverter(new PlaceUpdateRequestToEntity());
        registry.addConverter(new ReviewUpsertRequestToReview());
        registry.addConverter(new IncomeRequestToAggregate());
        registry.addConverter(new ExpenditureRequestToAggregate());
        registry.addConverter(new CurrencyConversionRequestToAggregate());
        registry.addConverter(new ActivityCreateRequestToEntity());
        registry.addConverter(new RouteUpdateRequestToEntity());
        registry.addConverter(new TravelPlanEntityToResponse());
        registry.addConverter(new ScheduleEntityToResponse());
    }

}
