package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class RoomToBasicInfoRoomOutputConverter implements Converter<Room, BasicInfoRoomOutput.BasicInfoRoomOutputBuilder> {

    @Override
    public BasicInfoRoomOutput.BasicInfoRoomOutputBuilder convert(Room source) {
        log.info("Started Converter - Room to BasicInfoRoomOutput");

        BasicInfoRoomOutput.BasicInfoRoomOutputBuilder output = BasicInfoRoomOutput.builder()
                .roomId(source.getId().toString())
                .price(source.getPrice())
                .floor(source.getRoomFloor())
                .bedSize(source.getBeds().stream()
                        .map(Bed::getBedSize)
                        .map(bedSize -> bedSize.name())
                        .collect(Collectors.toList()))
                .bathroomType(source.getBathroomType().toString())
                .bedCount(source.getBeds().size());

        log.info("Ended Converter - Room to BasicInfoRoomOutput");
        return output;
    }
}
