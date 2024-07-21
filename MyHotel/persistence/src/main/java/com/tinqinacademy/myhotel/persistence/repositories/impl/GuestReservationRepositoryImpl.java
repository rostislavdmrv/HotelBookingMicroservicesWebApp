package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.GuestReservation;
import com.tinqinacademy.myhotel.persistence.repositories.GuestReservationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuestReservationRepositoryImpl implements GuestReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    public GuestReservationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<GuestReservation> guestReservationRowMapper = new RowMapper<>() {
        @Override
        public GuestReservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return GuestReservation.builder()
                    .reservationId(UUID.fromString(rs.getString("reservation_id")))
                    .guestId(UUID.fromString(rs.getString("guest_id")))
                    .build();
        }
    };

    @Override
    public GuestReservation save(GuestReservation entity) {
        String sql = "INSERT INTO guest_reservations (reservation_id, guest_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                entity.getReservationId(),
                entity.getGuestId());
        return entity;
    }

    @Override
    public Optional<GuestReservation> findById(UUID id) {
        String sql = "SELECT * FROM guest_reservations WHERE reservation_id = ?";
        List<GuestReservation> guestReservations = jdbcTemplate.query(sql, guestReservationRowMapper, id);
        if (guestReservations.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(guestReservations.get(0));
        }
    }



    @Override
    public GuestReservation update(GuestReservation entity) {
        String sql = "UPDATE guest_reservations SET guest_id = ? WHERE reservation_id = ?";
        jdbcTemplate.update(sql,
                entity.getGuestId(),
                entity.getReservationId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM guest_reservations WHERE reservation_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<GuestReservation> findAll() {
        String sql = "SELECT * FROM guest_reservations";
        return jdbcTemplate.query(sql, guestReservationRowMapper);
    }
}
