package com.tinqinacademy.myhotel.core.config;

import com.tinqinacademy.myhotel.core.converters.RoomToBasicInfoRoomOutputConverter;
import com.tinqinacademy.myhotel.core.converters.VisitorInputToGuestConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RoomToBasicInfoRoomOutputConverter roomToBasicInfoRoomOutputConverter;
    private final VisitorInputToGuestConverter visitorInputToGuestConverter;

    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(roomToBasicInfoRoomOutputConverter);
        registry.addConverter(visitorInputToGuestConverter);

    }
}
