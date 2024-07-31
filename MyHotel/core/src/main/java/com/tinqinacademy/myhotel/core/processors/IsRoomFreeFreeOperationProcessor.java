package com.tinqinacademy.myhotel.core.processors;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.IsRoomFreeOperation;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IsRoomFreeFreeOperationProcessor extends BaseOperationProcessor implements IsRoomFreeOperation {

    private final RoomRepository roomRepository;
    private final ErrorHandler errorHandler;

    protected IsRoomFreeFreeOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.roomRepository = roomRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    public Either<ErrorWrapper, RoomOutput> process(RoomInput input) {
        log.info("Starts availability check for free room from {} to {}", input.getStartDate(), input.getEndDate());

        return Try.of(() -> processCheckRoomAvailability(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private RoomOutput processCheckRoomAvailability(RoomInput input) {
        validateInput(input);
        BathroomType bathroomType = validateBathroomType(input.getBathroomType());
        BedSize bedSize = validateBedSize(input.getBedSize());
        List<Room> availableRooms = findAvailableRooms(input.getStartDate(), input.getEndDate());
        List<Room> roomsMatchingCriteria = findRoomsByBedSizeAndBathroomType(bedSize, bathroomType);

        List<String> availableRoomIds = filterAvailableRoomsByCriteria(availableRooms, roomsMatchingCriteria);
        RoomOutput output = convertToOutput(availableRooms);

        log.info("End: Check results: {}", output);
        return output;
    }
    private BathroomType validateBathroomType(String bathroomTypeCode) {
        BathroomType bathroomType = BathroomType.getFromCode(bathroomTypeCode);
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType", "bathroomType", bathroomTypeCode);
        }
        return bathroomType;
    }
    private BedSize validateBedSize(String bedSizeCode) {
        BedSize bedSize = BedSize.getFromCode(bedSizeCode);
        if (bedSize.equals(BedSize.UNKNOWN)) {
            throw new ResourceNotFoundException("Bed", "bedSize", bedSizeCode);
        }
        return bedSize;
    }
    private List<Room> findAvailableRooms(LocalDate startDate, LocalDate endDate) {
        return roomRepository.findAvailableRooms(startDate, endDate)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "availability", "No rooms available for the given dates"));
    }

    private List<Room> findRoomsByBedSizeAndBathroomType(BedSize bedSize, BathroomType bathroomType) {
        return roomRepository.findRoomsByBedSizeAndBathroomType(bedSize, bathroomType);
    }
    private List<String> filterAvailableRoomsByCriteria(List<Room> availableRooms, List<Room> roomsMatchingCriteria) {
        List<String> availableRoomIds = new ArrayList<>();
        for (Room room : availableRooms) {
            if (roomsMatchingCriteria.contains(room)) {
                availableRoomIds.add(room.getId().toString());
            }
        }
        return availableRoomIds;
    }
    private RoomOutput convertToOutput(List<Room> availableRooms) {
        return conversionService.convert(availableRooms, RoomOutput.class);
    }


}
