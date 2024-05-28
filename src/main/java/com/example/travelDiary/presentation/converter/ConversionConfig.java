package com.example.travelDiary.presentation.converter;

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
    }

}
