package com.tinqinacademy.myhotel.core.services.system.impl;

import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.VisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies.RoomRenterOutput;
import com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.api.interfaces.system.SystemService;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.repositories.CreateRoomByAdminRepository;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private  final CreateRoomByAdminRepository createRoomByAdminRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;

    public SystemServiceImpl(CreateRoomByAdminRepository createRoomByAdminRepository, UserRepository userRepository, GuestRepository guestRepository) {
        this.createRoomByAdminRepository = createRoomByAdminRepository;
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
    }

    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        List<Guest> savedGuests = new ArrayList<>();
        List<User> savedUsers = new ArrayList<>();

        for (VisitorInput visitorInput : input.getVisitorInputs()) {

            Guest guest = Guest.builder()
                    .id(UUID.randomUUID())
                    .firstName(visitorInput.getFirstName())
                    .lastName(visitorInput.getLastName())
                    .birthday(generateRandomBirthday())
                    .idCardNumber(visitorInput.getIdCardNo())
                    .idIssueAuthority(visitorInput.getIdCardIssueAuthority())
                    .idIssueDate(visitorInput.getIdCardIssueDate())
                    .idValidity(visitorInput.getIdCardValidity())
                    .build();
            guestRepository.save(guest);
            savedGuests.add(guest);

            User user = User.builder()
                    .id(guest.getId())
                    .firstName(guest.getFirstName())
                    .lastName(guest.getLastName())
                    .userPassword(generateTemporaryPassword())
                    .email(guest.getFirstName().toLowerCase() + "." + guest.getLastName().toLowerCase() + "@example.com") // Генерирайте временен email
                    .birthday(guest.getBirthday())
                    .phoneNumber(visitorInput.getPhoneNo())
                    .build();
            userRepository.save(user);
            savedUsers.add(user);
        }


        RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();
        log.info("End : Successfully registered {}", input);
        return output;
    }

    private LocalDate generateRandomBirthday() {
        LocalDate currentDate = LocalDate.now();
        LocalDate latestDate = currentDate.minusYears(18);
        LocalDate earliestDate = currentDate.minusYears(100);

        long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate);
        long randomDays = ThreadLocalRandom.current().nextLong(daysBetween + 1);

        return earliestDate.plusDays(randomDays);
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    @Override
    public RoomRenterOccupancyOutput getRoomRentersOccupancies(RoomRenterOccupancyInput input) {
        log.info("Starts getting room renters occupancy {}", input);
        RoomRenterOccupancyOutput output = RoomRenterOccupancyOutput.builder().roomRenters((List<RoomRenterOutput>) input).build();
        log.info("End : Successfully getting room renters occupancy {}", input);
        return output;
    }

    @Override
    public CreateRoomOutput createNewRoom(CreateRoomInput input) {
        UUID uuid = UUID.randomUUID();
        log.info("Starts creating new room {}", input);
        Room room = Room.builder()
                .id(uuid)
                .roomFloor(input.getFloor())
                .roomNumber(input.getRoomNo())
                .bathroomType(BathroomType.getFromCode(input.getBathroomType()))
                .price(input.getPrice())
                .build();

        createRoomByAdminRepository.save(room);

        CreateRoomOutput output = CreateRoomOutput.builder().roomId(room.getId()).build();
        log.info("End : Successfully creating new room with ID {}", output);


        return output;
    }

    @Override
    public UpdateRoomOutput updateAlreadyExistRoom(UpdateRoomInput input) {
        log.info("Starts updating existing room {}", input);
        Room room = createRoomByAdminRepository.findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setRoomFloor(input.getFloor());
        room.setRoomNumber(input.getRoomNo());
        room.setBathroomType(BathroomType.getFromCode(input.getBathroomType()));
        room.setPrice(input.getPrice());

        createRoomByAdminRepository.update(room);



        UpdateRoomOutput output = UpdateRoomOutput.builder().roomId(room.getId().toString()).build();

        log.info("End: Successfully updated existing room with ID {}", output.getRoomId());
        return output;
    }

    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input) {

        log.info("Starts partially updating room with ID '{}' and details {}", input.getRoomId(), input);
        Room room = createRoomByAdminRepository.findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Room not found"));


        if (input.getFloor() != null) {
            room.setRoomFloor(input.getFloor());
        }
        if (input.getRoomNo() != null) {
            room.setRoomNumber(input.getRoomNo());
        }
        if (input.getBathroomType() != null) {
            room.setBathroomType(BathroomType.valueOf(input.getBathroomType()));
        }
        if (input.getPrice() != null) {
            room.setPrice(input.getPrice());
        }

        createRoomByAdminRepository.save(room);

        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder().roomId(room.getId().toString()).build();
        log.info("End: Successfully partially updated room with ID {}", output.getRoomId());

        return output;


    }

    @Override
    public DeleteRoomOutput deleteRooms(DeleteRoomInput input) {
        log.info("Starts deleting rooms {}", input);
        UUID roomId = UUID.fromString(input.getRoomId());
        createRoomByAdminRepository.deleteById(roomId);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();
        log.info("End: Successfully deleted room with ID {}", roomId);

        return output;
    }


}
