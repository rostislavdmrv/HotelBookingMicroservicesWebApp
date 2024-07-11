package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;

import java.util.List;

public interface HotelRepository {
    List<RoomInput> getAllRooms();
}
