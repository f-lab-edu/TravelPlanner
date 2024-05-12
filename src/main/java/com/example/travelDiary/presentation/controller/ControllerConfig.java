package com.example.travelDiary.presentation.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ControllerConfig {
    @Bean
    public RestTemplate REST() {
        return new RestTemplate();
    }
}
