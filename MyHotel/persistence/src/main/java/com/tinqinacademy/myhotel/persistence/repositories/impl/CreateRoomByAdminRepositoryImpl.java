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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class CreateRoomByAdminRepositoryImpl implements CreateRoomByAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public CreateRoomByAdminRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Room> roomRowMapper = (rs, rowNum) -> Room.builder()
            .id(UUID.fromString(rs.getString("id")))
            .roomFloor(rs.getInt("room_floor"))
            .roomNumber(rs.getString("room_number"))
            .bathroomType(BathroomType.getFromCode(rs.getString("bathroom_type")))
            .price(rs.getBigDecimal("price"))
            .build();

    private final RowMapper<Bed> bedRowMapper = (rs, rowNum) -> Bed.builder()
            .id(UUID.fromString(rs.getString("id")))
            .capacity(rs.getInt("capacity"))
            .bedSize(BedSize.getFromCode(rs.getString("bed")))
            .build();

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO rooms (id, room_floor, room_number, bathroom_type, price) VALUES (?, ?, ?, ?::BATHROOM_TYPE, ?)";
        jdbcTemplate.update(sql, room.getId(), room.getRoomFloor(), room.getRoomNumber(),
                room.getBathroomType().toString(), room.getPrice());

        if (room.getBeds() != null && !room.getBeds().isEmpty()) {
            for (Bed bed : room.getBeds()) {
                saveBed(bed);
                saveInRoomsBedsTable(room.getId(), bed.getId());
            }
        } else {
            List<Bed> allBeds = fetchAllBeds();
            List<Bed> randomBeds = selectRandomBeds(allBeds);
            room.setBeds(randomBeds);
            for (Bed bed : room.getBeds()) {
                saveInRoomsBedsTable(room.getId(), bed.getId());
            }
        }
        return room;
    }

    private void saveBed(Bed bed) {
        String sql = "INSERT INTO beds (id, capacity, bed_size) VALUES (?, ?, ?::bed_size)";
        jdbcTemplate.update(sql, bed.getId(), bed.getCapacity(), bed.getBedSize().toString());
    }

    private void saveInRoomsBedsTable(UUID roomId, UUID bedId) {
        String sql = "INSERT INTO rooms_beds (room_id, bed_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, roomId, bedId);
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
        String sql = "SELECT b.id, b.capacity, b.bed FROM beds b JOIN rooms_beds rb ON b.id = rb.bed_id WHERE rb.room_id = ?";
        return jdbcTemplate.query(sql, new Object[]{roomId}, bedRowMapper);
    }

    private List<Bed> fetchAllBeds() {
        String sql = "SELECT * FROM beds";
        return jdbcTemplate.query(sql, bedRowMapper);
    }

    private List<Bed> selectRandomBeds(List<Bed> allBeds) {
        int numberOfBeds = ThreadLocalRandom.current().nextInt(1, 4);
        Collections.shuffle(allBeds);
        return allBeds.subList(0, numberOfBeds);
    }

    @Override
    public Room update(Room room) {
        String sql = "UPDATE rooms SET room_floor = ?, room_number = ?, bathroom_type = ?::BATHROOM_TYPE, price = ? WHERE id = ?";
        jdbcTemplate.update(sql, room.getRoomFloor(), room.getRoomNumber(), room.getBathroomType().toString(), room.getPrice(), room.getId());
        return room;
    }

    @Override
    public void deleteById(UUID id) {
        String deleteFromRoomsBedsQuery = "DELETE FROM rooms_beds WHERE room_id = ?";
        jdbcTemplate.update(deleteFromRoomsBedsQuery, id);

        String deleteFromRoomsQuery = "DELETE FROM rooms WHERE id = ?";
        jdbcTemplate.update(deleteFromRoomsQuery, id);
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, roomRowMapper);
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM rooms";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public void deleteAll() {
        String deleteAllFromRoomsBedsQuery = "DELETE FROM rooms_beds";
        jdbcTemplate.update(deleteAllFromRoomsBedsQuery);

        String deleteAllFromRoomsQuery = "DELETE FROM rooms";
        jdbcTemplate.update(deleteAllFromRoomsQuery);
    }
}
