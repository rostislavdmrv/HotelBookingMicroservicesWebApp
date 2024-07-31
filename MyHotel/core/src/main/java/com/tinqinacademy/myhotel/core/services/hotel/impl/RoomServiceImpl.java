package com.tinqinacademy.myhotel.core.services.hotel.impl;

import com.tinqinacademy.myhotel.api.exceptions.NotAvailableException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.UserNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import com.tinqinacademy.myhotel.api.interfaces.room.RoomService;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    public RoomOutput getAvailableRooms(RoomInput input) {
        log.info("Starts availability check for free room from {} to {}",input.getStartDate(),input.getEndDate());

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType","bathroomType",input.getBathroomType());

        }
        BedSize bedSize = BedSize.getFromCode(input.getBedSize());
        if (bedSize.equals(BedSize.UNKNOWN)) {
            throw new ResourceNotFoundException("Bed","bedSize",input.getBedSize());

        }
        List<Room> availableRooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate())
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",input.getId()));

        List<Room> roomsMatchingCriteria = roomRepository.findRoomsByBedSizeAndBathroomType(bedSize, bathroomType);
        List<String> availableRoomIds = new ArrayList<>();
        for (Room room : availableRooms) {
            if (roomsMatchingCriteria.contains(room)) {
                availableRoomIds.add(room.getId().toString());
            }
        }
        RoomOutput output = conversionService.convert(availableRooms, RoomOutput.class);

        log.info("End: Check results: {}", output);
        return output;

    }

    @Override
    public BasicInfoRoomOutput getInfoForRoom(BasicInfoRoomInput input) {
        log.info("Starts getting basic information for room with ID: {}", input.getRoomId());

        UUID roomId = UUID.fromString(input.getRoomId());
        Room room = roomRepository.findByIdWithBeds(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

        List<Reservation> reservations = reservationRepository.findAllByRoomId(room.getId()).orElse(new ArrayList<>());

        List<LocalDate> datesOccupied = new ArrayList<>();
        for (Reservation reservation : reservations) {
            LocalDate startDate = reservation.getStartDate();
            LocalDate endDate = reservation.getEndDate();
            while (startDate.isBefore(endDate)) {
                datesOccupied.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }

        BasicInfoRoomOutput output = Objects.requireNonNull(conversionService.convert(room, BasicInfoRoomOutput.BasicInfoRoomOutputBuilder.class))
                .datesOccupied(datesOccupied).build();

        log.info("End: Found room info: {}", output);
        return output;
    }

    @Override
    public BookRoomOutput bookRoom(BookRoomInput input) {
        log.info("Stars booking room with ID: {} for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());

        UUID roomId = UUID.fromString(input.getRoomId());
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room","roomId",roomId.toString());
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

        List<Room> availableRooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate())
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

        if (!availableRooms.contains(room)) {
            throw new NotAvailableException(Messages.NOT_AVAILABLE_ROOM);
        }

        User user = userRepository
                .findByPhoneNumberAndFirstNameAndLastName(input.getPhoneNo(), input.getFirstName(), input.getLastName())
                .orElseThrow(() -> new UserNotFoundException(input.getFirstName(),input.getLastName(),input.getPhoneNo()));


        BigDecimal totalPrice = calculateTotalPrice(input.getStartDate(), input.getEndDate(), room.getPrice());

        Reservation reservation = Objects.requireNonNull(conversionService.convert(input, Reservation.ReservationBuilder.class)).room(room).user(user)
                .totalPrice(totalPrice).guests(Set.of()).build();

        reservationRepository.save(reservation);

        BookRoomOutput output = BookRoomOutput.builder().build();

        log.info("End: Room with ID {} successfully booked for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());
        return output;
    }
    private BigDecimal calculateTotalPrice(LocalDate startDate, LocalDate endDate, BigDecimal pricePerDay) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        return pricePerDay.multiply(BigDecimal.valueOf(daysBetween));
    }

    @Override
    public UnbookRoomOutput removeBookedRoom(UnbookRoomInput id) {
        log.info("Starts attempting to cancel reservation for room with ID: {} ",id.getBookingId());

        UUID bookingId = UUID.fromString(id.getBookingId());
        Reservation reservation = reservationRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation","bookingId",bookingId.toString()));;

        reservationRepository.delete(reservation);

        log.info("End: Reservation for room with ID: {} successfully cancelled!", bookingId);
        UnbookRoomOutput output = UnbookRoomOutput.builder().build();

        return output;
    }



}
