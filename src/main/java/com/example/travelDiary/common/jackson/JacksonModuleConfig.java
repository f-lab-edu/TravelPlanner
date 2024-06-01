package com.example.travelDiary.common.jackson;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonModuleConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(new NamedType(Income.class, "income"));
        objectMapper.registerSubtypes(new NamedType(Expenditure.class, "expenditure"));
        objectMapper.registerSubtypes(new NamedType(CurrencyConversion.class, "currency_conversion"));
        return objectMapper;
    }
}
