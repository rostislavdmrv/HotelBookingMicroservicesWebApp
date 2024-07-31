package com.tinqinacademy.myhotel.core.services.system.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.myhotel.api.exceptions.NoMatchException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import com.tinqinacademy.myhotel.core.exceptions.RoomForUpdateInput;
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

import java.io.IOException;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final BedRepository bedRepository;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;


    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);

        for (VisitorInput visitorInput : input.getVisitorInputs()) {
            UUID roomId = UUID.fromString(visitorInput.getRoomId());
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));

            Guest guest = conversionService.convert(visitorInput, Guest.class);

            Reservation reservation = reservationRepository.findByRoomIdAndStartDateAndEndDate(
                            room.getId(), visitorInput.getStartDate(), visitorInput.getEndDate())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "roomId", room.getId().toString()));

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

        Map<UUID, VisitorReportOutput> guestMap = new HashMap<>();

        //1. Search by startDate and endDate
        if (input.getStartDate() != null && input.getEndDate() != null) {

            List<Reservation> reservations = reservationRepository
                    .findByDateRange(input.getStartDate(), input.getEndDate())
                    .orElse(Collections.emptyList());

            for (Reservation booking : reservations) {
                for (Guest guest : booking.getGuests()) {
                    VisitorReportOutput guestOutput =
                            Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                    .startDate(booking.getStartDate())
                                    .endDate(booking.getEndDate())
                                    .build();
                    guestMap.putIfAbsent(guest.getId(), guestOutput);
                }
            }
        }

        //2. Search by guest details
        if (input.getFirstName() != null &&
                input.getLastName() != null &&
                input.getRoomNo() != null &&
                input.getIdCardNo() != null &&
                input.getIdCardValidity() != null &&
                input.getIdCardIssueAuthority() != null &&
                input.getIdCardIssueDate() != null) {

            List<Guest> matchingGuests = guestRepository.findMatchingGuests(
                    input.getFirstName(),
                    input.getLastName(),
                    input.getPhoneNo(),
                    input.getIdCardNo(),
                    input.getIdCardValidity().toString(),
                    input.getIdCardIssueAuthority(),
                    input.getIdCardIssueDate().toString()
            ).orElse(Collections.emptyList());

            for (Guest guest : matchingGuests) {
                List<Reservation> reservations = reservationRepository.findByGuestIdCardNumber(guest.getIdCardNumber())
                        .orElse(Collections.emptyList());

                if (reservations.isEmpty()) {
                    if (!guestMap.containsKey(guest.getId())) {
                        VisitorReportOutput guestOutput = conversionService.convert(guest, VisitorReportOutput.class);
                        guestMap.put(guest.getId(), guestOutput);
                    }
                } else {
                    for (Reservation booking : reservations) {
                        if (!guestMap.containsKey(guest.getId())) {
                            VisitorReportOutput guestOutput =
                                    Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                            .startDate(booking.getStartDate())
                                            .endDate(booking.getEndDate())
                                            .build();
                            guestMap.put(guest.getId(), guestOutput);
                        }
                    }
                }

            }
        }

        //3. Search by room number
        if (input.getRoomNo() != null) {
            Room room = roomRepository.findByRoomNumber(input.getRoomNo())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", "roomNo", input.getRoomNo()));

            List<Reservation> reservations = reservationRepository.findByRoomId(room.getId())
                    .orElse(Collections.emptyList());

            for (Reservation booking : reservations) {
                for (Guest guest : booking.getGuests()) {
                    if (!guestMap.containsKey(guest.getId())) {
                        VisitorReportOutput guestOutput =
                                Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                        .startDate(booking.getStartDate())
                                        .endDate(booking.getEndDate())
                                        .build();
                        guestMap.put(guest.getId(), guestOutput);
                    }
                }
            }
        }

        if (guestMap.isEmpty()) {
            throw new NoMatchException(Messages.NO_MATCHING);
        }

        List<VisitorReportOutput> values = new ArrayList<>(guestMap.values());
        ReportOutput output = ReportOutput.builder()
                .reports(values)
                .build();
        log.info("End : Successfully made report {}", output);
        return output;


    }

    @Override
    public CreateRoomOutput createNewRoom(CreateRoomInput input) {

        log.info("Starts creating new room {}", input);

        if (roomRepository.existsRoomByRoomNumber(input.getRoomNo())) {
            throw new ResourceNotFoundException("Room", "roomNo", input.getRoomNo());
        }

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType", "bathroomType", input.getBathroomType());

        }

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed", "bedSize", bedSize.toString()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));

        BathroomType bathroomType = BathroomType.getFromCode(input.getBathroomType());
        if (bathroomType.equals(BathroomType.UNKNOWN)) {
            throw new ResourceNotFoundException("BathroomType", "bathroomType", input.getBathroomType());

        }

        if (roomRepository.existsRoomByRoomNumber(input.getRoomNo())) {
            throw new ResourceNotFoundException("Room", "roomNo", input.getRoomNo());
        }

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed", "bedSize", bedSize.toString()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId.toString()));

        List<Bed> roomBeds = new ArrayList<>();
        for (String size : input.getBeds()) {
            BedSize bedSize = BedSize.valueOf(size.toUpperCase());
            Bed bed = bedRepository.findByBedSize(bedSize)
                    .orElseThrow(() -> new ResourceNotFoundException("Bed", "bedSize", bedSize.toString()));
            roomBeds.add(bed);
        }
        RoomForUpdateInput updateInput = Objects.requireNonNull(conversionService.convert(input, RoomForUpdateInput.RoomForUpdateInputBuilder.class)).beds(roomBeds).build();

        JsonNode roomNode = objectMapper.valueToTree(room);

        JsonNode inputNode = objectMapper.valueToTree(updateInput);

        try {
            JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
            JsonNode patchedNode = patch.apply(roomNode);

            Room patchedRoom = objectMapper.treeToValue(patchedNode, Room.class);

            roomRepository.save(patchedRoom);

            PartialUpdateRoomOutput output = conversionService.convert(patchedRoom, PartialUpdateRoomOutput.class);
            log.info("End: Successfully partially updated room with ID {}", output.getRoomId());

            return output;
        } catch (JsonPatchException | IOException e) {
            throw new RuntimeException("Failed to apply patch to room: " + e.getMessage(), e);
        }

    }
    private JsonNode applyPatch(JsonNode existingRoomNode, JsonNode updateRequestNode) {
        // Create a copy of the existing room node
        JsonNode patchedNode = existingRoomNode.deepCopy();

        // Iterate over fields in the update request and apply changes
        Iterator<Map.Entry<String, JsonNode>> fields = updateRequestNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            ((ObjectNode) patchedNode).set(field.getKey(), field.getValue());
        }

        return patchedNode;
    }


    @Override
    public DeleteRoomOutput deleteRooms(DeleteRoomInput input) {
        log.info("Starts deleting rooms {}", input);

        UUID roomId = UUID.fromString(input.getRoomId());

        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "roomId", roomId.toString());
        }

        roomRepository.deleteById(roomId);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();
        log.info("End: Successfully deleted room with ID {}", roomId);

        return output;
    }


}
