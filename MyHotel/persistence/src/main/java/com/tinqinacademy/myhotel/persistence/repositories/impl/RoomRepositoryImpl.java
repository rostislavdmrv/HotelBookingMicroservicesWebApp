package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class RoomRepositoryImpl implements RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Room> rowMapper = new RowMapper<Room>() {
        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Room.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .roomFloor(rs.getInt("room_floor"))
                    .roomNumber(rs.getString("room_number"))
                    .bathroomType(BathroomType.valueOf(rs.getString("bathroom_type").toUpperCase()))
                    .price(rs.getBigDecimal("price"))
                    .build();
        }
    };

    @Override
    public Room findById(UUID id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void save(Room room) {
        String sql = "INSERT INTO rooms (id, room_floor, room_number, bathroom_type, price) VALUES (?, ?, ?, ?::bathroom_type, ?)";
        jdbcTemplate.update(sql, room.getId(), room.getRoomFloor(), room.getRoomNumber(), room.getBathroomType().name().toLowerCase(), room.getPrice());
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }



}
