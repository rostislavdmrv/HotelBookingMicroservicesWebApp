package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RoomToPartialUpdateRoomOutputConverter implements Converter<Room, PartialUpdateRoomOutput> {


    @Override
    public PartialUpdateRoomOutput convert(Room source) {
        log.info("Started Converter - Room to PartialUpdateRoomOutput");

        PartialUpdateRoomOutput partUpdateRoomOutput = PartialUpdateRoomOutput.builder()
                .roomId(source.getId().toString())
                .build();

        log.info("Ended Converter - Room to PartialUpdateRoomOutput");
        return partUpdateRoomOutput;
    }
}
