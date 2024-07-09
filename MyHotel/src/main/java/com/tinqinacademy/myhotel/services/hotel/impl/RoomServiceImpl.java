package com.tinqinacademy.myhotel.services.hotel.impl;

import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomInput;
import com.tinqinacademy.myhotel.repositories.HotelRepository;
import com.tinqinacademy.myhotel.services.hotel.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final HotelRepository hotelRepository;

    public RoomServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
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
        BookRoomOutput output = BookRoomOutput.builder().build();

        log.info("End: Room with ID {} successfully booked for dates {} to {}", input.getRoomId(), input.getStartDate(), input.getEndDate());
        return output;
    }

    @Override
    public RoomForRemoveOutput removeBookedRoom(RoomForRemoveInput id) {
        log.info("Starts attempting to cancel reservation for room with ID: {} ", id.getRoomId());

        RoomForRemoveOutput output = RoomForRemoveOutput.builder().build();
        log.info("End: Reservation for room with ID: {} successfully cancelled!", id.getRoomId());

        return output;
    }


}
