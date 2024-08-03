package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Slf4j
public class PartialUpdateRoomInputToRoomConverter implements Converter<PartialUpdateRoomInput, Room> {
    @Override
    public Room convert(PartialUpdateRoomInput source) {
        log.info("Start converting PartialUpdateRoomInput to Room");
        Room room = Room.builder()
                .roomFloor(source.getRoomFloor())
                .roomNumber(source.getRoomNumber())
                .bathroomType(BathroomType.getFromCode(source.getBathroomType()))
                .price(source.getPrice())
                .build();
        log.info("End converting PartialUpdateRoomInput to Room");
        return room;
    }
}
