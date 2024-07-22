package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;

public interface CreateRoomByAdminRepository extends GenericRepository<Room> {

    public long count();
    public void deleteAll();
}
