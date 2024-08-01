package com.tinqinacademy.myhotel.core.processors.system;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Service

@Slf4j
public class CreateRoomOperationProcessor extends BaseOperationProcessor implements CreateRoomOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ErrorHandler errorHandler;

    protected CreateRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, BedRepository bedRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    public Either<ErrorWrapper, CreateRoomOutput> process(CreateRoomInput input) {
        log.info("Starts creating new room {}", input);

        return Try.of(() -> processCreateRoom(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private CreateRoomOutput processCreateRoom(CreateRoomInput input) {
        validateInput(input);
        validateBathroomType(input.getBathroomType());
        List<Bed> roomBeds = findBeds(input.getBeds());
        Room room = buildRoom(input, roomBeds);
        roomRepository.save(room);
        CreateRoomOutput output = convertToOutput(room);
        log.info("End : Successfully creating new room with ID {}", output.getRoomId());
        return output;
    }


    private void validateBathroomType(String bathroomTypeCode) {
        BathroomType bathroomType = BathroomType.getFromCode(bathroomTypeCode);
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType", "bathroomType", bathroomTypeCode);
        }
    }

    private List<Bed> findBeds(List<String> bedSizes) {
        List<Bed> roomBeds = new ArrayList<>();
        for (String size : bedSizes) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed", "bedSize", bedSize.toString()));
            roomBeds.add(bed);
        }
        return roomBeds;
    }

    private Room buildRoom(CreateRoomInput input, List<Bed> roomBeds) {
        return Objects.requireNonNull(conversionService.convert(input, Room.RoomBuilder.class))
                .beds(roomBeds)
                .build();
    }

    private CreateRoomOutput convertToOutput(Room room) {
        return conversionService.convert(room, CreateRoomOutput.class);
    }


}
