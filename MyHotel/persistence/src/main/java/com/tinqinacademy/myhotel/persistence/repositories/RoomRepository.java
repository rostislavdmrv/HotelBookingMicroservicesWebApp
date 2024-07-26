package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("""
            SELECT DISTINCT r FROM Room r JOIN r.beds b 
            WHERE b.bedSize = :bedSize AND r.bathroomType = :bathroomType
            """)
    List<Room> findRoomsByBedSizeAndBathroomType(
            @Param("bedSize") BedSize bedSize,
            @Param("bathroomType") BathroomType bathroomType);

    @Query(value = """
            SELECT r.*
            FROM rooms r
            LEFT JOIN reservations res ON r.id = res.room_id
                 AND res.start_date <= :endDate
                 AND res.end_date >= :startDate
            WHERE res.room_id IS NULL;
            """, nativeQuery = true)
    List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT r FROM Room r LEFT JOIN FETCH r.beds WHERE r.id = :roomId
            """)
    Optional<Room> findByIdWithBeds(@Param("roomId") UUID roomId);


    boolean existsRoomByRoomNumber(String roomNo);


    Optional<Room> findByRoomNumber(String roomNumber);







    @Query("""
           SELECT COUNT(r) FROM Room r WHERE r.roomNumber = ?1 
           """)
    boolean existsRoomNo(String roomNo);
    //Room update(Room entity);



    public long count();
    public void deleteAll();


}
