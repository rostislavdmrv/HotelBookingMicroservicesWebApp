package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;

import java.util.List;
import java.util.UUID;

public interface RoomRepository {
    Room findById(UUID id);
    List<Room> findAll();
    void save(Room room);
    void deleteById(UUID id);
}
