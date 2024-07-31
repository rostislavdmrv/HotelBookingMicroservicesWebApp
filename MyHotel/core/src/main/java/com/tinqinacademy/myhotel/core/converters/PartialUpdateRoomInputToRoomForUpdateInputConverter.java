package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.core.exceptions.RoomForUpdateInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PartialUpdateRoomInputToRoomForUpdateInputConverter implements Converter<PartialUpdateRoomInput, RoomForUpdateInput.RoomForUpdateInputBuilder> {
    @Override
    public RoomForUpdateInput.RoomForUpdateInputBuilder convert(PartialUpdateRoomInput source) {
        RoomForUpdateInput.RoomForUpdateInputBuilder room = RoomForUpdateInput.builder()
                .floor(source.getFloor())
                .roomNo(source.getRoomNo())
                .bathroomType(source.getBathroomType());
        return room;
    }
}
