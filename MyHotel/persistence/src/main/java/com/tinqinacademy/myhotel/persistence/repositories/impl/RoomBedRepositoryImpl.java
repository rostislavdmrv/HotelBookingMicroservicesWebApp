package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.RoomBed;
import com.tinqinacademy.myhotel.persistence.repositories.RoomBedRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomBedRepositoryImpl implements RoomBedRepository {
    private final JdbcTemplate jdbcTemplate;

    public RoomBedRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RoomBed> roomBedRowMapper = new RowMapper<>() {
        @Override
        public RoomBed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return RoomBed.builder()
                    .bedId(UUID.fromString(rs.getString("bed_id")))
                    .roomId(UUID.fromString(rs.getString("room_id")))
                    .build();
        }
    };

    @Override
    public RoomBed save(RoomBed entity) {
        String sql = "INSERT INTO room_beds (bed_id, room_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                entity.getBedId(),
                entity.getRoomId());
        return entity;
    }

    @Override
    public Optional<RoomBed> findById(UUID id) {
        String sql = "SELECT * FROM room_beds WHERE bed_id = ?";
        List<RoomBed> roomBeds = jdbcTemplate.query(sql, roomBedRowMapper, id);
        if (roomBeds.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(roomBeds.get(0));
        }
    }

    @Override
    public RoomBed update(RoomBed entity) {
        String sql = "UPDATE room_beds SET room_id = ? WHERE bed_id = ?";
        jdbcTemplate.update(sql,
                entity.getRoomId(),
                entity.getBedId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM room_beds WHERE bed_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<RoomBed> findAll() {
        String sql = "SELECT * FROM room_beds";
        return jdbcTemplate.query(sql, roomBedRowMapper);
    }
}
