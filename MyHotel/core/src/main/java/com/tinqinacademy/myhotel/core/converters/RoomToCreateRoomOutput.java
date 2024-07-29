package com.tinqinacademy.myhotel.core.converters;


import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RoomToCreateRoomOutput implements Converter<Room, CreateRoomOutput> {

    @Override
    public CreateRoomOutput convert(Room source) {
        log.info("Started Converter - Room to CreateRoomOutput");

        CreateRoomOutput createRoomOutput = CreateRoomOutput.builder()
                .roomId(source.getId())
                .build();

        log.info("Ended Converter - Room to CreateRoomOutput");
        return createRoomOutput;
    }
}
