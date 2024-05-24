package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.presentation.converter.location.PlaceUpdateRequestToEntity;
import com.example.travelDiary.presentation.converter.mediaFile.PreSignedUploadRequestToMediaFile;
import com.example.travelDiary.presentation.converter.review.ReviewUpsertRequestToReview;
import com.example.travelDiary.presentation.converter.travel.EventCreateRequestToEntity;
import com.example.travelDiary.presentation.converter.travel.ScheduleInsertRequestToEntity;
import com.example.travelDiary.presentation.converter.travel.ScheduleMetadataUpdateRequestToEntity;
import com.example.travelDiary.presentation.converter.travel.TravelPlanRequestToEntity;
import com.example.travelDiary.presentation.converter.wallet.CurrencyConversionRequestToAggregate;
import com.example.travelDiary.presentation.converter.wallet.ExpenditureRequestToAggregate;
import com.example.travelDiary.presentation.converter.wallet.IncomeRequestToAggregate;
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
        registry.addConverter(new EventCreateRequestToEntity());
    }

}