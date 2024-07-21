package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.CreateRoomByAdminRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CreateRoomByAdminRepositoryImpl implements CreateRoomByAdminRepository {

    private final JdbcTemplate jdbcTemplate;


    public CreateRoomByAdminRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Room> roomRowMapper = (ResultSet rs, int rowNum) -> {
        return Room.builder()
                .id(UUID.fromString(rs.getString("id")))
                .roomFloor(rs.getInt("room_floor"))
                .roomNumber(rs.getString("room_number"))
                .bathroomType(BathroomType.valueOf(rs.getString("bathroom_type")))
                .price(rs.getBigDecimal("price"))
                .build();
    };

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO rooms (id, room_floor, room_number, bathroom_type, price) VALUES (?, ?, ?, ?::BATHROOM_TYPE, ?)";

        jdbcTemplate.update(sql, room.getId(), room.getRoomFloor(), room.getRoomNumber(),
                room.getBathroomType().toString(), room.getPrice());
        return room;
    }

    @Override
    public Optional<Room> findById(UUID id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, id);
        if (rooms.isEmpty()) {
            return Optional.empty();
        } else {
            Room room = rooms.get(0);
            room.setBeds(fetchBedsForRoom(room.getId()));
            return Optional.of(room);
        }
    }

    private List<Bed> fetchBedsForRoom(UUID roomId) {
        String sql = "SELECT * FROM beds WHERE room_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Bed.builder()
                .id(UUID.fromString(rs.getString("id")))
                .capacity(rs.getInt("capacity"))
                .bedSize(BedSize.getFromCode(rs.getString("bed")))
                .build(), roomId);
    }

    @Override
    public Room update(Room room) {
        String sql = "UPDATE rooms SET room_floor = ?, room_number = ?, bathroom_type = ?::BATHROOM_TYPE, price = ? WHERE id = ?";
        jdbcTemplate.update(sql, room.getRoomFloor(), room.getRoomNumber(), room.getBathroomType().toString(), room.getPrice(), room.getId());
        return room;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, roomRowMapper);
    }
}
