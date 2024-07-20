package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuestRepositoryImpl implements GuestRepository {
    private final JdbcTemplate jdbcTemplate;

    public GuestRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Guest> guestRowMapper = new RowMapper<>() {
        @Override
        public Guest mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Guest.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .idCardNumber(rs.getString("id_card_number"))
                    .idIssueAuthority(rs.getString("id_issue_authority"))
                    .idIssueDate(rs.getDate("id_issue_date").toLocalDate())
                    .idValidity(rs.getDate("id_validity").toLocalDate())
                    .build();
        }
    };

    @Override
    public Guest save(Guest entity) {
        String sql = "INSERT INTO guests (id, first_name, last_name, birthday, id_card_number, id_issue_authority, id_issue_date, id_validity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        entity.setId(UUID.randomUUID());
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthday(),
                entity.getIdCardNumber(),
                entity.getIdIssueAuthority(),
                entity.getIdIssueDate(),
                entity.getIdValidity());
        return entity;
    }

    @Override
    public Optional<Guest> findById(UUID id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
        List<Guest> guests = jdbcTemplate.query(sql, guestRowMapper, id);
        if (guests.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(guests.get(0));
        }
    }

    @Override
    public Guest update(Guest entity) {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, birthday = ?, id_card_number = ?, id_issue_authority = ?, id_issue_date = ?, id_validity = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthday(),
                entity.getIdCardNumber(),
                entity.getIdIssueAuthority(),
                entity.getIdIssueDate(),
                entity.getIdValidity(),
                entity.getId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM guests WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Guest> findAll() {
        String sql = "SELECT * FROM guests";
        return jdbcTemplate.query(sql, guestRowMapper);
    }
}
