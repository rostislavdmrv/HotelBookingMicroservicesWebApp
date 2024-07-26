package com.tinqinacademy.myhotel.core.services.system.impl;

import com.tinqinacademy.myhotel.api.exceptions.NoMatchException;
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
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.*;
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
    private final ReservationRepository reservationRepository;
    private  final BedRepository bedRepository;


    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        for (VisitorInput visitorInput : input.getVisitorInputs()) {
            Room room = roomRepository.findById(visitorInput.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

            Guest guest = Guest.builder()
                    .firstName(visitorInput.getFirstName())
                    .lastName(visitorInput.getLastName())
                    .phoneNumber(visitorInput.getPhoneNo())
                    .idCardNumber(visitorInput.getIdCardNo())
                    .idCardValidity(visitorInput.getIdCardValidity())
                    .idCardIssueAuthority(visitorInput.getIdCardIssueAuthority())
                    .idCardIssueDate(visitorInput.getIdCardIssueDate())
                    .birthdate(visitorInput.getBirthdate())
                    .build();

            Reservation reservation = reservationRepository.findByRoomIdAndStartDateAndEndDate(
                            room.getId(), visitorInput.getStartDate(), visitorInput.getEndDate())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

            reservation.getGuests().add(guest);

            guestRepository.save(guest);
            reservationRepository.save(reservation);

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


        Room room = roomRepository.findByRoomNumber(input.getRoomNo())
                .orElseThrow(() -> new ResourceNotFoundException("No room found with number: " + input.getRoomNo()));

        // find all existing reservations that match: roomId, startDate, endDate
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateRange(
                        room.getId(), input.getStartDate(), input.getEndDate())
                .orElseThrow(() -> new ResourceNotFoundException("Reservations not found"));

        // find all existing guests that match: firstName, lastName, phoneNo,idCardNo, idCardValidity, idCardIssueAuthority, idCardIssueDate
        List<Guest> matchingGuests = guestRepository.findMatchingGuests(
                        input.getFirstName(),
                        input.getLastName(),
                        input.getPhoneNo(),
                        input.getIdCardNo(),
                        input.getIdCardValidity().toString(),
                        input.getIdCardIssueAuthority(),
                        input.getIdCardIssueDate().toString()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Guests nit  found"));

        List<VisitorReportOutput> guestOutputs = new ArrayList<>();

        // iterate through the reservations and through the guests, and find if any booking contains one of the matching guests
        for (Reservation res : reservations) {
            for (Guest guest : matchingGuests) {
                if (res.getGuests().contains(guest)) {

                    VisitorReportOutput visitors = VisitorReportOutput.builder()
                            .startDate(res.getStartDate())
                            .endDate(res.getEndDate())
                            .firstName(guest.getFirstName())
                            .lastName(guest.getLastName())
                            .phoneNo(guest.getPhoneNumber())
                            .idCardNo(guest.getIdCardNumber())
                            .idCardValidity(guest.getIdCardValidity())
                            .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                            .idCardIssueDate(guest.getIdCardIssueDate())
                            .build();
                    guestOutputs.add(visitors);
                }
            }
        }

        if (guestOutputs.isEmpty()) {
            throw new NoMatchException("No matching guests found for the provided criteria.");
        }


        ReportOutput output = ReportOutput.builder().roomRenters(guestOutputs).build();
        log.info("End : Successfully made report {}", output);
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

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed is not found"));
            roomBeds.add(bed);
        }


        Room room = Room.builder()
                .beds(roomBeds)
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


        Room room = roomRepository.findById(input.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + input.getRoomId()));

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException(input.getBathroomType());

        }

        if(BathroomType.getFromCode(input.getBathroomType()).equals(BathroomType.UNKNOWN)){
            throw new ResourceNotFoundException("No bathroom type found");
        }

        if (roomRepository.existsRoomByRoomNumber(input.getRoomNo()) && !room.getRoomNumber().equals(input.getRoomNo())) {
            throw new ResourceNotFoundException(input.getRoomNo());
        }

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed is not found"));
            roomBeds.add(bed);
        }

//        roomRepository.updateById(room.getId(), updatedRoom);
//        roomRepository.updateRoomBeds(roomBeds, room);

        room.setBeds(roomBeds);
        room.setBathroomType(bathroomType);
        room.setRoomNumber(input.getRoomNo());
        room.setRoomFloor(input.getFloor());
        room.setPrice(input.getPrice());
        Room updatedRoom = roomRepository.save(room);



        //Room updatedRoom = roomRepository.save(roomForUpdate);

        UpdateRoomOutput output = UpdateRoomOutput.builder().roomId(updatedRoom.getId().toString()).build();

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
