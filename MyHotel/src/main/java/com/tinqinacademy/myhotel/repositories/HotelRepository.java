package com.tinqinacademy.myhotel.repositories;

import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomInput;

import java.util.List;

public interface HotelRepository {
    List<RoomInput> getAllRooms();
}
