package com.tinqinacademy.myhotel.core.processors.hotel;

import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOperation;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
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
import java.util.Objects;
import java.util.UUID;


@Service
@Slf4j
public class BasicInfoRoomOperationProcessor extends BaseOperationProcessor<BasicInfoRoomInput,BasicInfoRoomOutput> implements BasicInfoRoomOperation  {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ErrorHandler errorHandler;

    protected BasicInfoRoomOperationProcessor(ConversionService conversionService, Validator validator, ErrorHandler errorHandler, ReservationRepository reservationRepository, RoomRepository roomRepository, ErrorHandler errorHandler1) {
        super(conversionService, validator, errorHandler);
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.errorHandler = errorHandler1;
    }


    @Override
    public Either<ErrorWrapper, BasicInfoRoomOutput> process(BasicInfoRoomInput input) {
        log.info("Starts getting basic information for room with ID: {}", input.getRoomId());

        return Try.of(() -> processRoomInfo(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private BasicInfoRoomOutput processRoomInfo(BasicInfoRoomInput input) {
        validateInput(input);

        UUID roomId = UUID.fromString(input.getRoomId());
        Room room = findRoomById(roomId);
        List<Reservation> reservations = findReservationsForRoom(room.getId());

        List<LocalDate> datesOccupied = calculateOccupiedDates(reservations);

        BasicInfoRoomOutput output = buildRoomOutput(room, datesOccupied);

        log.info("End: Found room info: {}", output);
        return output;
    }

    private Room findRoomById(UUID roomId) {
        return roomRepository.findByIdWithBeds(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));
    }
    private List<Reservation> findReservationsForRoom(UUID roomId) {
        return reservationRepository.findAllByRoomId(roomId).orElse(new ArrayList<>());
    }
    private List<LocalDate> calculateOccupiedDates(List<Reservation> reservations) {
        List<LocalDate> datesOccupied = new ArrayList<>();
        for (Reservation reservation : reservations) {
            LocalDate startDate = reservation.getStartDate();
            LocalDate endDate = reservation.getEndDate();
            while (startDate.isBefore(endDate)) {
                datesOccupied.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }
        return datesOccupied;
    }
    private BasicInfoRoomOutput buildRoomOutput(Room room, List<LocalDate> datesOccupied) {
        return Objects.requireNonNull(conversionService.convert(room, BasicInfoRoomOutput.BasicInfoRoomOutputBuilder.class))
                .datesOccupied(datesOccupied).build();
    }

}
