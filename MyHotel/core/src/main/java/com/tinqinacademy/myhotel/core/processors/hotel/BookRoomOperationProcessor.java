package com.tinqinacademy.myhotel.core.processors.hotel;

import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.NotAvailableException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.UserNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOperation;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Service
@Slf4j
public class BookRoomOperationProcessor extends BaseOperationProcessor<BookRoomInput,BookRoomOutput> implements BookRoomOperation {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    protected BookRoomOperationProcessor(ConversionService conversionService, Validator validator, ErrorHandler errorHandler, ReservationRepository reservationRepository, RoomRepository roomRepository, UserRepository userRepository, ErrorHandler errorHandler1) {
        super(conversionService, validator, errorHandler);
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.errorHandler = errorHandler1;
    }


    @Override
    public Either<ErrorWrapper, BookRoomOutput> process(BookRoomInput input) {
        log.info("Starts booking room with ID: {} for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());

        return Try.of(() -> processBookRoom(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }

    private BookRoomOutput processBookRoom(BookRoomInput input) {
        validateInput(input);
        UUID roomId = UUID.fromString(input.getRoomId());
        Room room = findRoomById(roomId);
        validateRoomAvailability(room, input.getStartDate(), input.getEndDate());
        User user = findUser(input);
        BigDecimal totalPrice = calculateTotalPrice(input.getStartDate(), input.getEndDate(), room.getPrice());
        Reservation reservation = createReservation(input, room, user, totalPrice);
        reservationRepository.save(reservation);

        BookRoomOutput output = BookRoomOutput.builder().build();

        log.info("End: Room with ID {} successfully booked for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());
        return output;
    }
    private Room findRoomById(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "roomId", roomId.toString());
        }
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));
    }
    private void validateRoomAvailability(Room room, LocalDate startDate, LocalDate endDate) {
        List<Room> availableRooms = roomRepository.findAvailableRooms(startDate, endDate)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", room.getId().toString()));

        if (!availableRooms.contains(room)) {
            throw new NotAvailableException(Messages.NOT_AVAILABLE_ROOM);
        }
    }
    private User findUser(BookRoomInput input) {
        return userRepository
                .findByPhoneNumberAndFirstNameAndLastName(input.getPhoneNo(), input.getFirstName(), input.getLastName())
                .orElseThrow(() -> new UserNotFoundException(input.getFirstName(), input.getLastName(), input.getPhoneNo()));
    }
    private BigDecimal calculateTotalPrice(LocalDate startDate, LocalDate endDate, BigDecimal roomPrice) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return roomPrice.multiply(BigDecimal.valueOf(daysBetween));
    }

    private Reservation createReservation(BookRoomInput input, Room room, User user, BigDecimal totalPrice) {
        return Objects.requireNonNull(conversionService.convert(input, Reservation.ReservationBuilder.class))
                .room(room)
                .user(user)
                .totalPrice(totalPrice)
                .guests(Set.of())
                .build();
    }

}
