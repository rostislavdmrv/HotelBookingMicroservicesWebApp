package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends GenericRepository<Reservation>{
    List<Reservation> findByRoomId(UUID roomId);
}
