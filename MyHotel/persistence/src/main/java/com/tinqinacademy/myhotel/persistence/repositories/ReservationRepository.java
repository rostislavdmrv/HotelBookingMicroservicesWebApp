package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<List<Reservation>> findAllByRoomId (UUID roomId);
    Optional<Reservation> findByRoomIdAndStartDateAndEndDate(UUID id, LocalDate startDate, LocalDate endDate);
    @Query("""
                     SELECT res FROM Reservation res 
                     WHERE res.room.id = :roomId 
                     AND ((res.startDate BETWEEN :startDate AND :endDate) 
                     OR (res.endDate BETWEEN :startDate AND :endDate))
            """)
    Optional<List<Reservation>> findByRoomIdAndDateRange(@Param("roomId") UUID roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<Reservation> findById(UUID id);
}
