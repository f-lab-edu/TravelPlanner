package com.example.travelDiary.presentation.dto;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register the subtypes for polymorphic deserialization
        mapper.registerSubtypes(new NamedType(Income.class, "income"));
        mapper.registerSubtypes(new NamedType(Expenditure.class, "expenditure"));
        mapper.registerSubtypes(new NamedType(CurrencyConversion.class, "currency_conversion"));

        return mapper;
    }
}
