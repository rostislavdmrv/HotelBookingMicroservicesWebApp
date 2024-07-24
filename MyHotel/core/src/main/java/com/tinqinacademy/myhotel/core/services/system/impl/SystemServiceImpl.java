package com.tinqinacademy.myhotel.core.services.system.impl;

import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.models.input.VisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.models.output.VisitorReportOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.api.interfaces.system.SystemService;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private  final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;


    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        List<Guest> savedGuests = new ArrayList<>();


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
    public ReportOutput reportByCriteria(ReportInput input) {
        log.info("Starts getting room renters occupancy {}", input);
        ReportOutput output = ReportOutput.builder().roomRenters((List<VisitorReportOutput>) input).build();
        log.info("End : Successfully getting room renters occupancy {}", input);
        return output;
    }

    @Override
    public CreateRoomOutput createNewRoom(CreateRoomInput input) {

        log.info("Starts creating new room {}", input);
//        if (roomRepository.existsRoomNo(input.getRoomNo())) {
//            throw new RoomNoAlreadyExistsException(input.getRoomNo());
//        }

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException(input.getBathroomType());

        }

        // Проверка за валидност на размерите на леглата
        List<BedSize> bedSizes = input.getBeds().stream()
                .map(BedSize::getFromCode)
                .peek(bedSize -> {
                    if (bedSize.equals(BedSize.UNKNOWN)) {
                        throw new ResourceNotFoundException(input.getBathroomType());
                    }
                })
                .collect(Collectors.toList());


        UUID uuid = UUID.randomUUID();

        Room room = Room.builder()
                .id(uuid)
                .roomFloor(input.getFloor())
                .roomNumber(input.getRoomNo())
                .bathroomType(bathroomType)
                .price(input.getPrice())
                .build();

        roomRepository.save(room);

        CreateRoomOutput output = CreateRoomOutput.builder().roomId(room.getId()).build();
        log.info("End : Successfully creating new room with ID {}", output);


        return output;
    }

    @Override
    public UpdateRoomOutput updateAlreadyExistRoom(UpdateRoomInput input) {
        log.info("Starts updating existing room {}", input);

        if (!roomRepository.existsById(input.getRoomId())) {
            throw new ResourceNotFoundException(input.getRoomId().toString());
        }

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException(input.getBathroomType());

        }

        // Проверка за валидност на размерите на леглата
        List<BedSize> bedSizes = input.getBeds().stream()
                .map(BedSize::getFromCode)
                .peek(bedSize -> {
                    if (bedSize.equals(BedSize.UNKNOWN)) {
                        throw new ResourceNotFoundException(input.getBathroomType());
                    }
                })
                .collect(Collectors.toList());

        Room room = Room.builder()
                .id(input.getRoomId())
                .beds(null)
                .bathroomType(bathroomType)
                .roomNumber(input.getRoomNo())
                .price(input.getPrice())
                .build();


        //Room updatedRoom = roomRepository.update(room);

        UpdateRoomOutput output = UpdateRoomOutput.builder().roomId(room.getId().toString()).build();

        log.info("End: Successfully updated existing room with ID {}", output.getRoomId());
        return output;
    }

    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input) {

        log.info("Starts partially updating room with ID '{}' and details {}", input.getRoomId(), input);
        Room room = roomRepository.findById(input.getRoomId())
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

        roomRepository.save(room);

        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder().roomId(room.getId().toString()).build();
        log.info("End: Successfully partially updated room with ID {}", output.getRoomId());

        return output;


    }

    @Override
    public DeleteRoomOutput deleteRooms(DeleteRoomInput input) {
        log.info("Starts deleting rooms {}", input);

        if (!roomRepository.existsById(input.getRoomId())) {
            throw new ResourceNotFoundException(input.getRoomId().toString());
        }
        UUID roomId = input.getRoomId();
        roomRepository.deleteById(roomId);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();
        log.info("End: Successfully deleted room with ID {}", roomId);

        return output;
    }


}
