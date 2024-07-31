package com.tinqinacademy.myhotel.core.processors;

import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;

@Service
public class PartialUpdateRoomOperationProcessor implements PartialUpdateRoomOperation {
    @Override
    public Either<ErrorWrapper, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
        return null;
    }
}
