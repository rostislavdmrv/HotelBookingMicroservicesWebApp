package com.tinqinacademy.myhotel.core.services.hotel.impl;

import com.tinqinacademy.myhotel.api.interfaces.room.RoomService;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.RoomForRemoveOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.RoomForRemoveInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.repositories.CreateRoomByAdminRepository;
import com.tinqinacademy.myhotel.persistence.repositories.HotelRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final CreateRoomByAdminRepository createRoomByAdminRepository;

    public RoomServiceImpl(HotelRepository hotelRepository, ReservationRepository reservationRepository, CreateRoomByAdminRepository createRoomByAdminRepository) {
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
        this.createRoomByAdminRepository = createRoomByAdminRepository;
    }

    public RoomOutput getAvailableRooms(RoomInput input) {
        log.info("Starts availability check for free room from {} to {}",input.getStartDate(),input.getEndDate());
        List<String> availableRoomIds = new ArrayList<>();

        List<RoomInput> allRooms = hotelRepository.getAllRooms();

        for (RoomInput room : allRooms) {
            if (room.getBedCount().equals(input.getBedCount()) &&
                    room.getBedSize().equals(input.getBedSize()) &&
                    room.getBathroomType().equals(input.getBathroomType()) &&
                    !isDateOverlap(room.getStartDate(), room.getEndDate(), input.getStartDate(), input.getEndDate())) {

                availableRoomIds.add(room.getId());
            }
        }

        RoomOutput output =RoomOutput.builder()
                .ids(availableRoomIds)
                .build(); ;
        log.info("End: Check results: {}", output);
        return output;
        //return RoomOutput.builder().ids(List.of("1","2","3")).build();

    }

    private boolean isDateOverlap(LocalDate roomStartDate, LocalDate roomEndDate, LocalDate queryStartDate, LocalDate queryEndDate) {
        return !roomEndDate.isBefore(queryStartDate) && !roomStartDate.isAfter(queryEndDate);
    }

    @Override
    public BasicInfoRoomOutput getInfoForRoom(BasicInfoRoomInput input) {
        log.info("Starts getting basic information for room with ID: {}", input.getRoomId());
        if ("7".equals(input.getRoomId())) {
            BasicInfoRoomOutput output = BasicInfoRoomOutput.builder()
                    .roomId("7")
                    .price(BigDecimal.valueOf(99.99))
                    .floor(2)
                    .bedSize("small_double")
                    .bathroomType("private")
                    .bedCount(1)
                    .datesOccupied(LocalDate.now())
                    .build();

            log.info("End: Found room info: {}", output);
            return output;
        }
        log.info("Room with ID: {} not found", input.getRoomId());
        return null;
    }


    @Override
    public BookRoomOutput bookRoom(BookRoomInput input) {
        log.info("Stars booking room with ID: {} for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());
        // just empty object

        Room room = createRoomByAdminRepository.findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Calculate total price based on room price per day
        BigDecimal totalPrice = calculateTotalPrice(input.getStartDate(), input.getEndDate(), room.getPrice());

        // Create a new reservation using Builder Pattern
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .roomId(UUID.fromString(input.getRoomId()))
                .userId(UUID.randomUUID()) // Placeholder for actual user ID
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .totalPrice(totalPrice)
                .build();

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
    public RoomForRemoveOutput removeBookedRoom(RoomForRemoveInput id) {
        log.info("Starts attempting to cancel reservation for room with ID: {} ", id.getRoomId());



        RoomForRemoveOutput output = RoomForRemoveOutput.builder().build();
        log.info("End: Reservation for room with ID: {} successfully cancelled!", id.getRoomId());

        return output;
    }


}
