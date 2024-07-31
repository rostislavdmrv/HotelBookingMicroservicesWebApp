package com.tinqinacademy.myhotel.core.processors;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOperation;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
public class DeleteRoomOperationProcessor extends BaseOperationProcessor implements DeleteRoomOperation {
    private final RoomRepository roomRepository;
    private final ErrorHandler errorHandler;

    protected DeleteRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.roomRepository = roomRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    public Either<ErrorWrapper, DeleteRoomOutput> process(DeleteRoomInput input) {
        return Try.of(() -> processDeleteRoom(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }

    private DeleteRoomOutput processDeleteRoom(DeleteRoomInput input) {
        UUID roomId = UUID.fromString(input.getRoomId());

        checkRoomExists(roomId);
        deleteRoomById(roomId);

        DeleteRoomOutput output = createDeleteRoomOutput();
        log.info("End: Successfully deleted room with ID {}", roomId);

        return output;
    }

    private void checkRoomExists(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "roomId", roomId.toString());
        }
    }
    private void deleteRoomById(UUID roomId) {
        roomRepository.deleteById(roomId);
    }
    private DeleteRoomOutput createDeleteRoomOutput() {
        return DeleteRoomOutput.builder().build();
    }
}
