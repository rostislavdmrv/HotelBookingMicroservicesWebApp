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
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import com.tinqinacademy.myhotel.persistence.repositories.CreateRoomByAdminRepository;
import com.tinqinacademy.myhotel.persistence.repositories.HotelRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final CreateRoomByAdminRepository createRoomByAdminRepository;
    private final BedRepository bedRepository;

    public RoomServiceImpl(HotelRepository hotelRepository, ReservationRepository reservationRepository, CreateRoomByAdminRepository createRoomByAdminRepository, BedRepository bedRepository) {
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
        this.createRoomByAdminRepository = createRoomByAdminRepository;
        this.bedRepository = bedRepository;
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


        Optional<Room> roomOptional = createRoomByAdminRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();


            List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());

            // Проверка дали стаята има поне една резервация
            if (!reservations.isEmpty()) {
                // Извлечете първата резервация (ако има само една)
                Reservation reservation = reservations.get(0);

                // Изчислете заетите дати за резервацията
                List<LocalDate> occupiedDates = generateDateRange(reservation.getStartDate(), reservation.getEndDate());

                // Създайте изходния обект
                BasicInfoRoomOutput output = BasicInfoRoomOutput.builder()
                        .roomId(room.getId().toString())
                        .price(room.getPrice())
                        .floor(room.getRoomFloor())
                        .bedSize(room.getBeds().isEmpty() ? "N/A" : room.getBeds().get(0).getBedSize().toString())
                        .bathroomType(room.getBathroomType().toString())
                        .bedCount(room.getBeds().size())
                        .datesOccupied(occupiedDates) // Списък на заетите дати
                        .build();

                log.info("End: Found room info: {}", output);
                return output;
            } else {
                log.info("No reservations found for room with ID: {}", input.getRoomId());
                return BasicInfoRoomOutput.builder()
                        .roomId(room.getId().toString())
                        .price(room.getPrice())
                        .floor(room.getRoomFloor())
                        .bedSize(room.getBeds().isEmpty() ? "N/A" : room.getBeds().get(0).getBedSize().toString())
                        .bathroomType(room.getBathroomType().toString())
                        .bedCount(room.getBeds().size())
                        .datesOccupied(Collections.emptyList())
                        .build();
            }
        } else {
            log.info("Room with ID: {} not found", input.getRoomId());
            return null;
        }
    }

    private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();

        // Проверете дали периодът е валиден
        if (startDate.isBefore(endDate)) {
            LocalDate currentDate = startDate.plusDays(1); // Започнете от деня след startDate
            while (currentDate.isBefore(endDate)) {
                dates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }

        return dates;
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
        UUID roomId = UUID.fromString(id.getRoomId());
        log.info("Starts attempting to cancel reservation for room with ID: {} ", roomId);


        // Find reservations by roomId
        List<Reservation> reservations = reservationRepository.findByRoomId(roomId);

        if (!reservations.isEmpty()) {
            for (Reservation reservation : reservations) {
                // Delete each reservation
                reservationRepository.deleteById(reservation.getId());

            }
            log.info("End: Reservation for room with ID: {} successfully cancelled!", reservations.get(0).getRoomId());
        } else {

            log.info("No reservation found for room with ID: {}", reservations.get(0).getRoomId());
        }
        RoomForRemoveOutput output = RoomForRemoveOutput.builder().build();

        return output;
    }



}
