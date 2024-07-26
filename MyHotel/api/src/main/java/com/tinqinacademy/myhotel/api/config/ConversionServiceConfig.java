package com.tinqinacademy.myhotel.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConversionServiceConfig {

    @Bean
    public ConversionService conversionService(){
        var conversionService = new DefaultConversionService();
        return conversionService;

    }
}
