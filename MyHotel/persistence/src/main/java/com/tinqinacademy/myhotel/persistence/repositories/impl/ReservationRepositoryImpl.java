package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

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
            Reservation reservation = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new Reservation(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("room_id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getDate("end_date").toLocalDate(),
                            rs.getBigDecimal("total_price")
                    )
            );
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
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Reservation(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("room_id")),
                        UUID.fromString(rs.getString("user_id")),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBigDecimal("total_price")
                )
        );
    }
}
