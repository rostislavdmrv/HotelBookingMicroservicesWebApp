package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("""
           SELECT COUNT(r) FROM Room r WHERE r.roomNumber = ?1 
           """)
    boolean existsRoomNo(String roomNo);
    //Room update(Room entity);



    public long count();
    public void deleteAll();
}
