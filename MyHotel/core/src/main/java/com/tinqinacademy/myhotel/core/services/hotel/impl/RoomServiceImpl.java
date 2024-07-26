package com.tinqinacademy.myhotel.core.services.hotel.impl;

import com.tinqinacademy.myhotel.api.exceptions.NotAvailableException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.UserNotFoundException;
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
        List<Room> availableRooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate());
        List<Room> roomsMatchingCriteria = roomRepository.findRoomsByBedSizeAndBathroomType(bedSize, bathroomType);
        List<UUID> availableRoomIds = new ArrayList<>();

        for (Room room : availableRooms) {
            if (roomsMatchingCriteria.contains(room)) {
                availableRoomIds.add(room.getId());
            }
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

        Room room = roomRepository.findByIdWithBeds(input.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));

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

        BasicInfoRoomOutput output = BasicInfoRoomOutput.builder()
                .roomId(room.getId())
                .price(room.getPrice())
                .floor(room.getRoomFloor())
                .bedSize(room.getBeds().stream()
                        .map(Bed::getBedSize) // Получавате `BetSize` или `Enum` тип
                        .map(bedSize -> bedSize.name()) // Преобразувате в низ, ако е `Enum`
                        .collect(Collectors.toList()))
                .bathroomType(room.getBathroomType().toString())
                .bedCount(room.getBeds().size())
                .datesOccupied(datesOccupied)
                .build();



        log.info("End: Found room info: {}", output);
        return output;


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
        Room room = roomRepository.findByIdWithBeds(input.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        List<Room> availableRooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate());

        if (!availableRooms.contains(room)) {
            throw new NotAvailableException("Room is not available for the selected dates");
        }

        User user = userRepository
                .findByPhoneNumberAndFirstNameAndLastName(input.getPhoneNo(), input.getFirstName(), input.getLastName())
                .orElseThrow(() -> new UserNotFoundException("no user found"));


        BigDecimal totalPrice = calculateTotalPrice(input.getStartDate(), input.getEndDate(), room.getPrice());

        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user)
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .totalPrice(totalPrice)
                .guests(Set.of()) // Empty set, later will have endpoint for adding guests for certain reservation
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

        log.info("End: Reservation for room with ID: {} successfully cancelled!", reservationOptional.get());
        UnbookRoomOutput output = UnbookRoomOutput.builder().build();


        return output;
    }



}
