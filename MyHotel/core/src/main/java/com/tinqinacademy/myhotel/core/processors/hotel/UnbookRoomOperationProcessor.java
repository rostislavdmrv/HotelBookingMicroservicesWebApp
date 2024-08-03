package com.tinqinacademy.myhotel.core.processors.hotel;


import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOperation;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UnbookRoomOperationProcessor extends BaseOperationProcessor implements UnbookRoomOperation {
    private final ReservationRepository reservationRepository;
    private final ErrorHandler errorHandler;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    protected UnbookRoomOperationProcessor(ConversionService conversionService, Validator validator, ReservationRepository reservationRepository, ErrorHandler errorHandler, RoomRepository roomRepository, UserRepository userRepository) {
        super(conversionService, validator);
        this.reservationRepository = reservationRepository;
        this.errorHandler = errorHandler;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorWrapper, UnbookRoomOutput> process(UnbookRoomInput input) {
        log.info("Starts attempting to cancel reservation for room with ID: {} ", input.getBookingId());

        return Try.of(() -> processCancelReservationById(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private UnbookRoomOutput processCancelReservationById(UnbookRoomInput input) {
        validateInput(input);
        UUID bookingId = UUID.fromString(input.getBookingId());
        Reservation reservation = findReservationById(bookingId);
        deleteReservation(reservation);

        log.info("End: Reservation for room with ID: {} successfully cancelled!", bookingId);
        return UnbookRoomOutput.builder().build();
    }
    private Reservation findReservationById(UUID bookingId) {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "bookingId", bookingId.toString()));
    }
    private void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

}
