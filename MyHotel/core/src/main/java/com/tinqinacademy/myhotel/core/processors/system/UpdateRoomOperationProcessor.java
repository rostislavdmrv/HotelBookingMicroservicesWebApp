package com.tinqinacademy.myhotel.core.processors.system;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
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
import java.util.UUID;


@Service
@Slf4j
public class UpdateRoomOperationProcessor extends BaseOperationProcessor implements UpdateRoomOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ErrorHandler errorHandler;

    protected UpdateRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, BedRepository bedRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    public Either<ErrorWrapper, UpdateRoomOutput> process(UpdateRoomInput input) {
        log.info("Starts updating existing room {}", input);

        return Try.of(() -> processUpdateRoom(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private UpdateRoomOutput processUpdateRoom(UpdateRoomInput input) {
        validateInput(input);
        UUID roomId = UUID.fromString(input.getRoomId());
        Room room = findRoom(roomId);
        BathroomType bathroomType = validateBathroomType(input.getBathroomType());
        checkRoomNumberUniqueness(input.getRoomNo());
        List<Bed> roomBeds = findBeds(input.getBeds());

        updateRoom(room, input, bathroomType, roomBeds);

        roomRepository.save(room);

        UpdateRoomOutput output = convertToOutput(room);
        log.info("End: Successfully updated existing room with ID {}", output.getRoomId());

        return output;
    }
    private Room findRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));
    }
    private BathroomType validateBathroomType(String bathroomTypeCode) {
        BathroomType bathroomType = BathroomType.getFromCode(bathroomTypeCode);
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType", "bathroomType", bathroomTypeCode);
        }
        return bathroomType;
    }
    private void checkRoomNumberUniqueness(String roomNo) {
        if (roomRepository.existsRoomByRoomNumber(roomNo)) {
            throw new ResourceNotFoundException("Room", "roomNo", roomNo);
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
    private void updateRoom(Room room, UpdateRoomInput input, BathroomType bathroomType, List<Bed> roomBeds) {
        room.setBeds(roomBeds);
        room.setBathroomType(bathroomType);
        room.setRoomNumber(input.getRoomNo());
        room.setRoomFloor(input.getFloor());
        room.setPrice(input.getPrice());
    }
    private UpdateRoomOutput convertToOutput(Room room) {
        return conversionService.convert(room, UpdateRoomOutput.class);
    }
}
