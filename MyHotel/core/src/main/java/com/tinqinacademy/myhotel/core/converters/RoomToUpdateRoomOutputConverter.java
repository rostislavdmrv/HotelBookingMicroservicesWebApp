package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RoomToUpdateRoomOutputConverter implements Converter<Room, UpdateRoomOutput> {
    @Override
    public UpdateRoomOutput convert(Room source) {
        log.info("Started Converter - Room to UpdateRoomOutput");

        UpdateRoomOutput updateRoomOutput = UpdateRoomOutput.builder()
                .roomId(source.getId().toString())
                .build();

        log.info("Ended Converter - Room to UpdateRoomOutput");
        return updateRoomOutput;
    }
}
