package com.example.travelDiary.presentation.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConversionConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        // Register your converter with the conversion service
        conversionService.addConverter(new TravelPlanRequestToEntity());
        return conversionService;
    }
}
