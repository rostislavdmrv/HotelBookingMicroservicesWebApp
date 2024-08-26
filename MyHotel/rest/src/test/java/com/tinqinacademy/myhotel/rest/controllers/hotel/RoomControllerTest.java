package com.tinqinacademy.myhotel.rest.controllers.hotel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.restapiroutes.RestApiRoutes;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.init.DataSeeder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class RoomControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private DataSeeder dataSeeder;


    @BeforeEach
    public void setup() throws Exception {
        dataSeeder.run(new ApplicationArguments() {
            @Override
            public String[] getSourceArgs() {
                return new String[0];
            }

            @Override
            public Set<String> getOptionNames() {
                return Set.of();
            }

            @Override
            public boolean containsOption(String name) {
                return false;
            }

            @Override
            public List<String> getOptionValues(String name) {
                return List.of();
            }

            @Override
            public List<String> getNonOptionArgs() {
                return List.of();
            }
        });

        List<Bed> beds = bedRepository.findAll();

        Room room = Room.builder()
                .price(BigDecimal.valueOf(59.99))
                .roomFloor(3)
                .roomNumber("36A")
                .bathroomType(BathroomType.PRIVATE)
                .beds(List.of(beds.get(0), beds.get(1)))
                .build();
        room = roomRepository.save(room);


        Reservation reservation = Reservation.builder()
                .room(room)
                .userId(UUID.fromString("56fc4621-a5f4-442b-82bb-cdd532ba4d33"))
                .startDate(LocalDate.of(2024, 10, 10))
                .endDate(LocalDate.of(2024, 10, 12))
                .totalPrice(BigDecimal.valueOf(300))
                .guests(new HashSet<>())
                .build();
        reservation = reservationRepository.save(reservation);

    }


    @AfterEach
    public void afterEach() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        bedRepository.deleteAll();
        guestRepository.deleteAll();
    }


    @Test
    void get_ids_of_available_rooms_Ok() throws Exception {
        mvc.perform(get(RestApiRoutes.CHECK_AVAILABILITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", "2024-10-20")
                        .param("endDate", "2024-10-22")
                        .param("bedCount", "2")
                        .param("bathroomType", "private")
                        .param("bedSize", "double")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(3))); // Очакваме 2 налични стаи, създадени от DataSeeder
    }

    @Test
    void get_room_info_by_id_returns_Ok() throws Exception {
        String roomId = roomRepository.findAll().getFirst().getId().toString();

        MvcResult getRoomInfo = mvc.perform(get(RestApiRoutes.RETRIEVE_BASIC_INFO, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = getRoomInfo.getResponse().getContentAsString();
        BasicInfoRoomOutput getRoomBasicInfoOutput = mapper.readValue(jsonResponse, BasicInfoRoomOutput.class);

        assertEquals(1, getRoomBasicInfoOutput.getFloor());
        assertEquals("private", getRoomBasicInfoOutput.getBathroomType());
    }

    @Test
    void get_room_info_by_id_returns_not_found() throws Exception {
        UUID roomId = UUID.randomUUID();

        mvc.perform(get(RestApiRoutes.RETRIEVE_BASIC_INFO, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void book_room_returns_created() throws Exception {
        String roomId = roomRepository.findAll().getFirst().getId().toString();
        String userId = UUID.randomUUID().toString();

        mvc.perform(get(RestApiRoutes.BOOK_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2024-09-06\",\"endDate\":\"2024-09-10\",\"firstName\":\"Gabriela\",\"lastName\":\"Ivanova\",\"userId\":\"" + userId + "\"}")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void unbook_room_returns_ok() throws Exception {
        String bookingId = reservationRepository.findAll().getFirst().getId().toString();
        String roomId = roomRepository.findAll().getFirst().getId().toString();

        mvc.perform(get(RestApiRoutes.DELETE_RESERVATION, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + roomId + "\"}")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unbook_room_returns_not_found() throws Exception {
        String bookingId = reservationRepository.findAll().getFirst().getId().toString();

        mvc.perform(get(RestApiRoutes.DELETE_RESERVATION, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + UUID.randomUUID().toString() + "\"}")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }


}