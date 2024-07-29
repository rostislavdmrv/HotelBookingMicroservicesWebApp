package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RoomsToRoomOutputConverter implements Converter<List<Room>, RoomOutput> {
    @Override
    public RoomOutput convert(List<Room> source) {
        log.info("Started Converter - Room to CheckAvailableRoomOutput");

        List<String> ids = source.stream().map(room -> room.getId().toString()).toList();

        RoomOutput roomOutput = RoomOutput.builder().ids(ids).build();

        log.info("Ended Converter - Room to CheckAvailableRoomOutput");
        return roomOutput;
    }
}
