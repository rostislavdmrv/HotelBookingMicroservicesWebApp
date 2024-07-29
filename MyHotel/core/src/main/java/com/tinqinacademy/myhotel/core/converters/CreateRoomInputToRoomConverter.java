package com.tinqinacademy.myhotel.core.converters;


import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateRoomInputToRoomConverter implements Converter<CreateRoomInput, Room.RoomBuilder> {

    @Override
    public Room.RoomBuilder convert(CreateRoomInput source) {
        log.info("Started Converter - CreateRoomInput to Room.RoomBuilder");
        Room.RoomBuilder room = Room.builder()
                .roomNumber(source.getRoomNo())
                .roomFloor(source.getFloor())
                .bathroomType(BathroomType.getFromCode(source.getBathroomType()))
                .price(source.getPrice());

        log.info("Ended Converter - CreateRoomInput to Room.RoomBuilder");
        return room;

    }
}
