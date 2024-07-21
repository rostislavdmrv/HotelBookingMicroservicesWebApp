package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;


    private final RowMapper<Reservation> reservationRowMapper = new RowMapper<>() {
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Reservation.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .roomId(UUID.fromString(rs.getString("room_id")))
                    .userId(UUID.fromString(rs.getString("user_id")))
                    .startDate(rs.getDate("start_date").toLocalDate())
                    .endDate(rs.getDate("end_date").toLocalDate())
                    .totalPrice(rs.getBigDecimal("total_price"))
                    .build();
        }
    };

    public ReservationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reservation save(Reservation entity) {
        String sql = "INSERT INTO reservations (id, room_id, user_id, start_date, end_date, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, entity.getId(), entity.getRoomId(), entity.getUserId(), entity.getStartDate(), entity.getEndDate(), entity.getTotalPrice());
        return entity;
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try {
            Reservation reservation = jdbcTemplate.queryForObject(sql, new Object[]{id}, reservationRowMapper);
            return Optional.ofNullable(reservation);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Reservation update(Reservation entity) {
        String sql = "UPDATE reservations SET room_id = ?, user_id = ?, start_date = ?, end_date = ?, total_price = ? WHERE id = ?";
        jdbcTemplate.update(sql, entity.getRoomId(), entity.getUserId(), entity.getStartDate(), entity.getEndDate(), entity.getTotalPrice(), entity.getId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    @Override
    public List<Reservation> findByRoomId(UUID roomId) {
        String sql = "SELECT * FROM reservations WHERE room_id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, roomId);
    }
}
