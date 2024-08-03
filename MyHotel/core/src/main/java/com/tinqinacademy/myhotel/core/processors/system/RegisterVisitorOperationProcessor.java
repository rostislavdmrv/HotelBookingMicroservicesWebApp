package com.tinqinacademy.myhotel.core.processors.system;

import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.models.input.VisitorInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOperation;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class RegisterVisitorOperationProcessor extends BaseOperationProcessor<RegisterVisitorInput,RegisterVisitorOutput> implements RegisterVisitorOperation {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final ErrorHandler errorHandler;

    protected RegisterVisitorOperationProcessor(ConversionService conversionService, Validator validator, ErrorHandler errorHandler, RoomRepository roomRepository, ReservationRepository reservationRepository, GuestRepository guestRepository, ErrorHandler errorHandler1) {
        super(conversionService, validator, errorHandler);
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.errorHandler = errorHandler1;
    }


    @Override
    public Either<ErrorWrapper, RegisterVisitorOutput> process(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        return Try.of(() -> processRegisterVisitors(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private RegisterVisitorOutput processRegisterVisitors(RegisterVisitorInput input) {
        validateInput(input);
        for (VisitorInput visitorInput : input.getVisitorInputs()) {

            UUID roomId = UUID.fromString(visitorInput.getRoomId());
            Room room = findRoomById(roomId);

            Guest guest = convertToGuest(visitorInput);

            Reservation reservation = findReservationByRoomIdAndDates(room.getId(), visitorInput.getStartDate(), visitorInput.getEndDate());

            addGuestToReservation(reservation, guest);

            saveGuest(guest);
            saveReservation(reservation);
        }

        log.info("End: Successfully registered {}", input);
        return RegisterVisitorOutput.builder().build();
    }

    private Room findRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));
    }
    private Guest convertToGuest(VisitorInput visitorInput) {
        return conversionService.convert(visitorInput, Guest.class);
    }
    private Reservation findReservationByRoomIdAndDates(UUID roomId, LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByRoomIdAndStartDateAndEndDate(roomId, startDate, endDate)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "roomId", roomId.toString()));
    }
    private void addGuestToReservation(Reservation reservation, Guest guest) {
        reservation.getGuests().add(guest);
    }
    private void saveGuest(Guest guest) {
        guestRepository.save(guest);
    }
    private void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }


}
