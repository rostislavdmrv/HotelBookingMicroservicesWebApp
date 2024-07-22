package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .userPassword(rs.getString("user_password"))
                    .email(rs.getString("email"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .phoneNumber(rs.getString("phone_number"))
                    .build();
        }
    };

    @Override
    public User save(User entity) {
        String sql = "INSERT INTO users (id, first_name, last_name, user_password, email, birthday, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getBirthday(),
                entity.getPhoneNumber());
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    @Override
    public User update(User entity) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, user_password = ?, email = ?, birthday = ?, phone_number = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getBirthday(),
                entity.getPhoneNumber(),
                entity.getId());
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    @Override
    public Optional<UUID> findUserIdByPhoneNumber(String phoneNumber) {
        String sql = "SELECT id FROM users WHERE phone_number = ?";


        return null;
    }
}
