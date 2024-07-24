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

    //boolean existsByRoomIdAndBetwenStartAndEndDate(UUID roomId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT DISTINCT r.id
                FROM Room r
                JOIN r.beds b
                LEFT JOIN Reservation res ON r.id = res.roomId
                WHERE (res.startDate IS NULL OR res.endDate IS NULL OR res.startDate >= :endDate OR res.endDate <= :startDate)
                AND (:bathroomType IS NULL OR r.bathroomType = :bathroomType)
                AND (:bedSize IS NULL OR b.bedSize = :bedSize)
                AND (:bedCount IS NULL OR (SELECT COUNT(rb) FROM r.beds rb) = :bedCount)
            """)
    List<UUID> searchForAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("bathroomType") String bathroomType,
            @Param("bedSize") String bedSize,
            @Param("bedCount") Integer bedCount);

    List<Reservation> findByRoomId(UUID roomId);

    Optional<Reservation> findById(UUID id);
}
