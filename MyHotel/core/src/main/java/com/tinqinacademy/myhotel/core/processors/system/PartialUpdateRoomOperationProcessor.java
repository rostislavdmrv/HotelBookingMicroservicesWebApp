package com.tinqinacademy.myhotel.core.processors.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class PartialUpdateRoomOperationProcessor extends BaseOperationProcessor<PartialUpdateRoomInput,PartialUpdateRoomOutput> implements PartialUpdateRoomOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper objectMapper;

    protected PartialUpdateRoomOperationProcessor(ConversionService conversionService, Validator validator, ErrorHandler errorHandler, RoomRepository roomRepository, BedRepository bedRepository, ObjectMapper objectMapper) {
        super(conversionService, validator, errorHandler);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public Either<ErrorWrapper, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
        log.info("Starts partially updating room with ID '{}' and details {}", input.getRoomId(), input);

        return Try.of(() -> {
                    validateInput(input);
                    UUID roomId = UUID.fromString(input.getRoomId());
                    Room room = findRoomById(roomId);

                    Room converted = conversionService.convert(input, Room.class);

                    if (input.getBeds() != null && !input.getBeds().isEmpty()) {
                        List<Bed> roomBeds = convertBedSizesToBeds(input.getBeds());
                        converted.setBeds(roomBeds);
                    }
                    Room patchedRoom = applyPatchToRoom(converted, room);
                    roomRepository.save(patchedRoom);
                    PartialUpdateRoomOutput output = convertToOutput(patchedRoom);
                    log.info("End: Successfully partially updated room with ID {}", output.getRoomId());

                    return output;
                })
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }

    private Room findRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));
    }

    private List<Bed> convertBedSizesToBeds(List<String> bedSizes) {
        List<Bed> roomBeds = new ArrayList<>();
        for (String size : bedSizes) {
            Bed bed = findBedBySize(size);
            roomBeds.add(bed);
        }
        return roomBeds;
    }
    private Bed findBedBySize(String bed) {
        BedSize bedSize = BedSize.valueOf(bed.toUpperCase());
        return bedRepository.findByBedSize(bedSize)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", "bedSize", bedSize.toString()));
    }

    private Room applyPatchToRoom(Room input, Room room) {
        JsonNode inputNode = objectMapper.valueToTree(input);
        JsonNode roomNode = objectMapper.valueToTree(room);

        try {
            JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
            JsonNode patchedNode = patch.apply(roomNode);

            return objectMapper.treeToValue(patchedNode, Room.class);
        } catch (JsonPatchException | IOException e) {
            throw new RuntimeException("Failed to apply patch to room: " + e.getMessage(), e);
        }
    }

    private PartialUpdateRoomOutput convertToOutput(Room room) {
        return conversionService.convert(room, PartialUpdateRoomOutput.class);
    }
}
