package com.tinqinacademy.myhotel.core.processors.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PartialUpdateRoomOperationProcessor extends BaseOperationProcessor implements PartialUpdateRoomOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper objectMapper;
    private final ErrorHandler errorHandler;

    protected PartialUpdateRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, BedRepository bedRepository, ObjectMapper objectMapper, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.objectMapper = objectMapper;
        this.errorHandler = errorHandler;

    }

    @Override
    public Either<ErrorWrapper, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
        log.info("Starts partially updating room with ID '{}' and details {}", input.getRoomId(), input);

        return Try.of(() -> {
                    validateInput(input);
                    UUID roomId = UUID.fromString(input.getRoomId());
                    Room room = findRoomById(roomId);

                    if (input.getBeds() != null) {
                        updateBedsInInputNode(input);
                    }
                    Room patchedRoom = applyPatchToRoom(input, room);
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

    private void updateBedsInInputNode(PartialUpdateRoomInput input) {
        JsonNode inputNode = objectMapper.valueToTree(input);
        ArrayNode bedsArrayNode = (ArrayNode) inputNode.get("beds");

        if (bedsArrayNode != null) {
            List<Bed> beds = bedsArrayNode.findValuesAsText("").stream()
                    .map(this::findOrCreateBed)
                    .collect(Collectors.toList());

            ArrayNode updatedBedsNode = objectMapper.createArrayNode();
            beds.forEach(bed -> updatedBedsNode.add(objectMapper.valueToTree(bed)));

            ((ObjectNode) inputNode).set("beds", updatedBedsNode);
        }
    }
    private Bed findOrCreateBed(String bedSize) {
        return bedRepository.findByBedSize(BedSize.getFromCode(bedSize))
                .orElse(new Bed(UUID.randomUUID(),BedSize.getFromCode(bedSize), 2, null, null)); // Примерни стойности
    }

    private Room applyPatchToRoom(PartialUpdateRoomInput input, Room room) {
        JsonNode roomNode = objectMapper.valueToTree(room);
        JsonNode inputNode = objectMapper.valueToTree(input);

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
