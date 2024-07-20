package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class BedRepositoryImpl implements BedRepository {

    private final JdbcTemplate jdbcTemplate;

    public BedRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Bed> bedRowMapper = new RowMapper<>() {
        @Override
        public Bed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Bed.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .bedSize(BedSize.valueOf(rs.getString("bed")))
                    .capacity(rs.getInt("capacity"))
                    .build();
        }
    };

    @Override
    public Bed save(Bed entity) {
        String sql = "INSERT INTO beds (id, bed, capacity) VALUES (?, ?::bed_size, ?)";
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getBedSize().toString(),
                entity.getCapacity());
        return entity;
    }

    @Override
    public Optional<Bed> findById(UUID id) {
        String sql = "SELECT * FROM beds WHERE id = ?";
        List<Bed> beds = jdbcTemplate.query(sql, bedRowMapper, id);
        if (beds.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(beds.get(0));
        }
    }

    @Override
    public Bed update(Bed entity) {
        String sql = "UPDATE beds SET bed = ?::bed_size, capacity = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                entity.getBedSize().toString(),
                entity.getCapacity(),
                entity.getId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM beds WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Bed> findAll() {
        String sql = "SELECT * FROM beds";
        return jdbcTemplate.query(sql, bedRowMapper);
    }
}

