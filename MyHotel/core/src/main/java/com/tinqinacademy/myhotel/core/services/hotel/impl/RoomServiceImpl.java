package com.tinqinacademy.myhotel.core.services.hotel.impl;

import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.interfaces.room.RoomService;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {


    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;



    public RoomOutput getAvailableRooms(RoomInput input) {
        log.info("Starts availability check for free room from {} to {}",input.getStartDate(),input.getEndDate());

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException(input.getBathroomType());

        }
        BedSize bedSize = BedSize.getFromCode(input.getBedSize());
        if (bedSize.equals(BedSize.UNKNOWN)) {
            throw new ResourceNotFoundException(input.getBedSize());

        }

        // Проверка за валидност на размерите на леглата
//        List<BedType> bedSizes = input.getBedSize().stream()
//                .map(BedType::getFromCode)
//                .peek(bedSize -> {
//                    if (bedSize.equals(BedType.UNKNOWN)) {
//                        throw new ResourceNotFoundException(input.getBathroomType());
//                    }
//                })
//                .collect(Collectors.toList());




        List<UUID> availableRoomIds = reservationRepository.searchForAvailableRooms(input.getStartDate(),input.getEndDate(),input.getBathroomType(),input.getBedSize(),input.getBedCount());


        RoomOutput output =RoomOutput.builder()
                .ids(availableRoomIds)
                .build();
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


        Optional<Room> roomOptional = roomRepository.findById(input.getRoomId());
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

        if (!roomRepository.existsById(input.getRoomId())) {
            throw new ResourceNotFoundException(input.getRoomId().toString());
        }


//        if(reservationRepository.existsByRoomIdAndBetwenStartAndEndDate(
//                input.getRoomId(),
//                input.getStartDate(),
//                input.getEndDate())){
//            throw new NotAvailableException("Room with id " + input.getRoomId() + " is not available");
//        }


        Room room = roomRepository.findById(input.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Calculate total price based on room price per day
        BigDecimal totalPrice = calculateTotalPrice(input.getStartDate(), input.getEndDate(), room.getPrice());

        // Create a new reservation using Builder Pattern
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .roomId(input.getRoomId())
                .userId(UUID.randomUUID())
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
    public UnbookRoomOutput removeBookedRoom(UnbookRoomInput id) {

        log.info("Starts attempting to cancel reservation for room with ID: {} ",id.getRoomId());

        Optional<Reservation> reservationOptional =
                reservationRepository.findById(id.getRoomId());

        if(reservationOptional.isEmpty()){
            throw new ResourceNotFoundException(id.getRoomId().toString());
        }

        reservationRepository.delete(reservationOptional.get());

        log.info("End: Reservation for room with ID: {} successfully cancelled!", reservationOptional.get().getRoomId());
        UnbookRoomOutput output = UnbookRoomOutput.builder().build();


        return output;
    }



}
