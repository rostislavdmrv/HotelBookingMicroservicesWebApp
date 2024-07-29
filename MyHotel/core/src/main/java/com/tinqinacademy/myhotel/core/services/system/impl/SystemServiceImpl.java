package com.tinqinacademy.myhotel.core.services.system.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.myhotel.api.exceptions.NoMatchException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private  final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private  final BedRepository bedRepository;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;


    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        for (VisitorInput visitorInput : input.getVisitorInputs()) {
            UUID roomId = UUID.fromString(visitorInput.getRoomId());
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

            Guest guest = conversionService.convert(visitorInput, Guest.class);

            Reservation reservation = reservationRepository.findByRoomIdAndStartDateAndEndDate(
                            room.getId(), visitorInput.getStartDate(), visitorInput.getEndDate())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation","roomId",room.getId().toString()));

            reservation.getGuests().add(guest);

            guestRepository.save(guest);
            reservationRepository.save(reservation);

        }

        RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();
        log.info("End : Successfully registered {}", input);
        return output;
    }


    @Override
    public ReportOutput reportByCriteria(ReportInput input) {
        log.info("Starts getting room renters occupancy {}", input);

        Room room = roomRepository.findByRoomNumber(input.getRoomNo())
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomNo",input.getRoomNo()));

        // find all existing reservations that match: roomId, startDate, endDate
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateRange(
                        room.getId(), input.getStartDate(), input.getEndDate())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation","roomId",room.getId().toString()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Guest","roomId",room.getId().toString()));

        List<VisitorReportOutput> guestOutputs = new ArrayList<>();

        // iterate through the reservations and through the guests, and find if any booking contains one of the matching guests
        for (Reservation res : reservations) {
            for (Guest guest : matchingGuests) {
                if (res.getGuests().contains(guest)) {

                    VisitorReportOutput visitor = Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                            .startDate(res.getStartDate())
                            .endDate(res.getEndDate())
                            .build();

                    guestOutputs.add(visitor);
                }
            }
        }

        if (guestOutputs.isEmpty()) {
            throw new NoMatchException(Messages.NO_MATCHING);
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
            throw new ResourceNotFoundException("BathroomType","bathroomType",input.getBathroomType());

        }

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed","bedSize",bedSize.toString()));
            roomBeds.add(bed);
        }

        Room room = Objects.requireNonNull(conversionService.convert(input, Room.RoomBuilder.class)).beds(roomBeds).build();

        roomRepository.save(room);

        CreateRoomOutput output = conversionService.convert(room, CreateRoomOutput.class);
        log.info("End : Successfully creating new room with ID {}", output);

        return output;
    }

    @Override
    public UpdateRoomOutput updateAlreadyExistRoom(UpdateRoomInput input) {
        log.info("Starts updating existing room {}", input);

        UUID roomId = UUID.fromString(input.getRoomId());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType","bathroomType",input.getBathroomType());

        }

        if (roomRepository.existsRoomByRoomNumber(input.getRoomNo())) {
            throw new ResourceNotFoundException("Room","roomNo",input.getRoomNo());
        }

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed","bedSize",bedSize.toString()));
            roomBeds.add(bed);
        }

        room.setBeds(roomBeds);
        room.setBathroomType(bathroomType);
        room.setRoomNumber(input.getRoomNo());
        room.setRoomFloor(input.getFloor());
        room.setPrice(input.getPrice());

        roomRepository.save(room);

        UpdateRoomOutput output = conversionService.convert(room, UpdateRoomOutput.class);
        log.info("End: Successfully updated existing room with ID {}", output.getRoomId());
        return output;
    }

    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input)  {

        log.info("Starts partially updating room with ID '{}' and details {}", input.getRoomId(), input);

        UUID roomId = UUID.fromString(input.getRoomId());
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomId.toString()));

        // Update room properties with the provided values
        room.setRoomFloor(input.getFloor());
        room.setRoomNumber(input.getRoomNo());

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType","bathroomType",input.getBathroomType());
        }
        room.setBathroomType(bathroomType);

        room.setPrice(input.getPrice());

        // Handle bed updates if needed
        if (input.getBeds() != null && !input.getBeds().isEmpty()) {
            List<Bed> roomBeds = new ArrayList<>();
            for (String size : input.getBeds()) {
                BedSize bedSize = BedSize.valueOf(size.toUpperCase());
                Bed bed = bedRepository.findByBedSize(bedSize)
                        .orElseThrow(() -> new ResourceNotFoundException("Bed","bedSize",bedSize.toString()));
                roomBeds.add(bed);
            }
            room.setBeds(roomBeds);
        }


        roomRepository.save(room);

        PartialUpdateRoomOutput output = conversionService.convert(room, PartialUpdateRoomOutput.class);
        log.info("End: Successfully partially updated room with ID {}", output.getRoomId());

        return output;
    }


    @Override
    public DeleteRoomOutput deleteRooms(DeleteRoomInput input) {
        log.info("Starts deleting rooms {}", input);

        UUID roomId = UUID.fromString(input.getRoomId());

        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room","roomId",roomId.toString());
        }

        roomRepository.deleteById(roomId);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();
        log.info("End: Successfully deleted room with ID {}", roomId);

        return output;
    }


}
