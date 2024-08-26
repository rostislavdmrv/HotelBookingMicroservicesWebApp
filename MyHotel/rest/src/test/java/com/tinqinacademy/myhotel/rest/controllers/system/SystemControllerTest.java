package com.tinqinacademy.myhotel.rest.controllers.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.myhotel.api.models.input.VisitorInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.restapiroutes.RestApiRoutes;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class SystemControllerTest {
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
    void register_guest_returns_created() throws Exception {
        String roomId = roomRepository.findAll().getFirst().getId().toString();
        Reservation reservation = reservationRepository.findAll().getFirst();

        List<VisitorInput> guests = List.of(
                VisitorInput.builder()
                        .startDate(reservation.getStartDate())
                        .endDate(reservation.getEndDate())
                        .firstName("John")
                        .lastName("Doe")
                        .phoneNo("+359865214732")
                        .idCardNo("58254444")
                        .roomId(roomId)
                        .idCardValidity(LocalDate.of(2025, 10, 15))
                        .idCardIssueAuthority("MVR VARNA")
                        .idCardIssueDate(LocalDate.of(2020, 10, 11))
                        .birthdate(LocalDate.of(1990, 10, 11))
                        .build()
        );

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputs(guests)
                .build();

        mvc.perform(post(RestApiRoutes.REGISTER_NEW_GUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }
    @Test
    void register_guest_returns_bad_request() throws Exception {
        String roomId = roomRepository.findAll().getFirst().getId().toString();
        Reservation reservation = reservationRepository.findAll().getFirst();

        List<VisitorInput> guests = List.of(
                VisitorInput.builder()
                        .startDate(reservation.getStartDate())
                        .endDate(reservation.getEndDate())
                        .firstName("John")
                        .lastName("Doe")
                        .phoneNo("+359865214732")
                        .idCardNo("")
                        .roomId(roomId)
                        .idCardValidity(LocalDate.of(2025, 10, 15))
                        .idCardIssueAuthority("MVR VARNA")
                        .idCardIssueDate(LocalDate.of(2020, 10, 11))
                        .birthdate(LocalDate.of(1990, 10, 11))
                        .build()
        );

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputs(guests)
                .build();

        mvc.perform(post(RestApiRoutes.REGISTER_NEW_GUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void get_report_returns_not_found() throws Exception {
        mvc.perform(get(RestApiRoutes.REPORT_VISITORS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_report_using_room_number_returns_ok() throws Exception {
        Room room = roomRepository.findAll().getFirst();

        mvc.perform(get(RestApiRoutes.REPORT_VISITORS)
                        .param("roomNo", room.getRoomNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void create_room_returns_created() throws Exception {
        List<String> bedSizes = List.of("single", "double");

        CreateRoomInput input = CreateRoomInput.builder()
                .roomNo("79C")
                .beds(bedSizes)
                .bathroomType("private")
                .floor(2)
                .price(new BigDecimal("59.99"))
                .build();

        mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }
    @Test
    void create_room_returns_bad_request() throws Exception {
        List<String> bedSizes = List.of("single", "double");

        CreateRoomInput input = CreateRoomInput.builder()
                .roomNo("79C")
                .beds(bedSizes)
                .bathroomType("private")
                .floor(-2)
                .price(new BigDecimal("59.99"))
                .build();

        mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_whole_room_returns_ok() throws Exception {
        Room room = roomRepository.findAll().getFirst();
        List<String> bedSizes = List.of("single", "double");
        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomNo("106A")
                .beds(bedSizes)
                .bathroomType("shared")
                .floor(4)
                .price(new BigDecimal("19.99"))
                .build();

        mvc.perform(put(RestApiRoutes.UPDATE_ROOM, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void update_partial_room_returns_ok() throws Exception {
        Room room = roomRepository.findAll().getFirst();
        List<String> bedSizes = List.of("single", "double");
        PartialUpdateRoomInput input = PartialUpdateRoomInput.builder()
                .roomNumber("407C")
                .bathroomType("shared")
                .roomFloor(4)
                .price(new BigDecimal("19.99"))
                .beds(bedSizes)
                .build();

        mvc.perform(patch(RestApiRoutes.PART_UPDATE_ROOM, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }



    @Test
    void delete_room_returns_not_found() throws Exception {
        UUID roomId = UUID.randomUUID();

        mvc.perform(delete(RestApiRoutes.REMOVE_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }
    @Test
    void delete_room_bad_request() throws Exception {
        mvc.perform(delete(RestApiRoutes.REMOVE_ROOM,"")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_room_returns_ok() throws Exception {
        Room room = roomRepository.findAll().getFirst();

        mvc.perform(delete(RestApiRoutes.REMOVE_ROOM, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

}