package com.tinqinacademy.myhotel.persistence.repositories.init;

import com.tinqinacademy.myhotel.persistence.models.entities.*;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Order(1)
public class DataSeeder implements ApplicationRunner {
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public DataSeeder(BedRepository bedRepository, RoomRepository roomRepository, UserRepository userRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.bedRepository = bedRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedBeds();
        seedRooms();
        seedUsers();
        seedGuests();
        seedBookings();
    }

    private void seedBeds() {
        if (bedRepository.count() != 0) {
            log.info("DataSeeder - didn't seed any beds.");
            return;
        }

        Bed singleBed = Bed.builder()
                //.id(UUID.randomUUID())
                .bedSize(BedSize.SINGLE)
                .capacity(1)
                .build();


        Bed smallDoubleBed = Bed.builder()
                .bedSize(BedSize.SMALL_DOUBLE)
                .capacity(2)
                .build();

        Bed doubleBed = Bed.builder()
                .bedSize(BedSize.DOUBLE)
                .capacity(2)
                .build();

        Bed kingSizeBed = Bed.builder()
                .bedSize(BedSize.KING_SIZE)
                .capacity(2)
                .build();

        Bed queenSizeBed = Bed.builder()
                .bedSize(BedSize.QUEEN_SIZE)
                .capacity(2)
                .build();

        bedRepository.saveAll(List.of(singleBed, smallDoubleBed, doubleBed, kingSizeBed, queenSizeBed));
        log.info("DataSeeder - seeded beds.");
    }

    private void seedRooms() {
        if (roomRepository.count() != 0) {
            log.info("DataSeeder - didn't seed any rooms.");
            return;
        }

        Bed sampleBed1 = bedRepository.findAll().get(0);
        Bed sampleBed2 = bedRepository.findAll().get(1);
        Bed sampleBed3 = bedRepository.findAll().get(1);

        Room room1 = Room.builder()
                .price(new BigDecimal("89.99"))
                .roomFloor(1)
                .roomNumber("A101")
                .bathroomType(BathroomType.PRIVATE)
                .beds(List.of(sampleBed1, sampleBed2))
                .build();

        Room room2 = Room.builder()
                .price(new BigDecimal("60.00"))
                .roomFloor(2)
                .roomNumber("B227")
                .bathroomType(BathroomType.SHARED)
                .beds(List.of(sampleBed3))
                .build();

        roomRepository.saveAll(List.of(room1, room2));
        log.info("DataSeeder - seeded rooms.");
    }

    private void seedUsers() {
        if (userRepository.count() != 0) {
            log.info("DataSeeder - didn't seed any users.");
            return;
        }

        User user1 = User.builder()
                .email("mira@gmail.com")
                .userPassword("1236548")
                .firstName("Mira")
                .lastName("Ivanova")
                .phoneNumber("+359898456532")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("martinn20@mail.com")
                .userPassword("nhj5d5")
                .firstName("Martin")
                .lastName("Marinov")
                .phoneNumber("+359963147541")
                .birthdate(LocalDate.of(2001, 5, 11))
                .build();

        userRepository.saveAll(List.of(user1, user2));
        log.info("DataSeeder - seeded users.");
    }

    private void seedGuests() {
        if (guestRepository.count() != 0) {
            log.info("DataSeeder - didn't seed any guests.");
            return;
        }

        Guest guest1 = Guest.builder()
                .firstName("Petar")
                .lastName("Petrov")
                .phoneNumber("+359987654321")
                .idCardNumber("ID123456")
                .idCardValidity(LocalDate.of(2025, 1, 1))
                .idCardIssueAuthority("MVR-VARNA")
                .idCardIssueDate(LocalDate.of(2015, 1, 1))
                .birthdate(LocalDate.of(1995, 1, 1))
                .build();

        Guest guest2 = Guest.builder()
                .firstName("Alex")
                .lastName("Penev")
                .phoneNumber("+359834913413")
                .idCardNumber("ID531333")
                .idCardValidity(LocalDate.of(2027, 7, 7))
                .idCardIssueAuthority("MVR-SOFIA")
                .idCardIssueDate(LocalDate.of(2011, 9, 12))
                .birthdate(LocalDate.of(1999, 11, 25))
                .build();

        guestRepository.saveAll(List.of(guest1, guest2));
        log.info("DataSeeder - seeded guests.");
    }

    private void seedBookings() {
        if (reservationRepository.count() != 0) {
            log.info("DataSeeder - didn't seed any bookings.");
            return;
        }

        Room room = roomRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);
        Guest guest = guestRepository.findAll().get(0);

        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .totalPrice(new BigDecimal("350.00"))
                .guests(Set.of(guest))
                .build();

        reservationRepository.save(reservation);
        log.info("DataSeeder - seeded a reservation.");
    }
}
