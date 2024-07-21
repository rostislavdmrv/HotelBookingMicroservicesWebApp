package com.tinqinacademy.myhotel.rest.controllers.system;

import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.api.interfaces.system.SystemService;
import com.tinqinacademy.myhotel.rest.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController

public class SystemController {

    private  final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }


    @Operation(summary = "Registers a new renter ", description = "Registers a new renter in the system with specific details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered renter"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to register a new renter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(RestApiRoutes.REGISTER_NEW_GUEST)
    public ResponseEntity<RegisterVisitorOutput> registerVisitorAsRenter(@Valid @RequestBody RegisterVisitorInput input) {


        RegisterVisitorOutput result = systemService.registerVisitorAsRenter(input);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }



    @Operation(summary = "Retrieves room renters occupancies", description = "Retrieves information about all visitors currently occupying the room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room occupancy information"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to access room occupancy"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(RestApiRoutes.RENTER_OCCUPANCIES)
    public ResponseEntity<RoomRenterOccupancyOutput> getRoomRentersOccupancies(
             @RequestParam LocalDate startDate,
             @RequestParam LocalDate endDate,
             @RequestParam String firstName,
             @RequestParam String lastName,
             @RequestParam String phoneNo,
             @RequestParam String idCardNo,
             @RequestParam LocalDate idCardValidity,
             @RequestParam String idCardIssueAuthority,
             @RequestParam LocalDate idCardIssueDate,
             @RequestParam String roomNo) {

        RoomRenterOccupancyInput input = RoomRenterOccupancyInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNo(phoneNo)
                .idCardNo(idCardNo)
                .idCardValidity(idCardValidity)
                .idCardIssueAuthority(idCardIssueAuthority)
                .idCardIssueDate(idCardIssueDate)
                .roomNo(roomNo)
                .build();


        RoomRenterOccupancyOutput result = systemService.getRoomRentersOccupancies(input);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @Operation(summary = "Creates a new room with the provided details", description = "Creates a room with specific parameters that will later serve as a template to save a room from a user ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully create a room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to create this room"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(RestApiRoutes.CREATE_ROOM)
    public ResponseEntity<CreateRoomOutput> createNewRoomInSystem(@Valid @RequestBody CreateRoomInput input) {


        CreateRoomOutput result = systemService.createNewRoom(input);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }

    @Operation(summary = "Updates a room with the provided details", description = "Updates the specified room with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to update this room"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(RestApiRoutes.UPDATE_ROOM)
    public ResponseEntity<UpdateRoomOutput> updateAlreadyCreatedRoomInSystem(@PathVariable("roomId") String roomId, @RequestBody @Valid UpdateRoomInput input) {


        UpdateRoomInput updated = input.toBuilder().roomId(roomId).build();
        UpdateRoomOutput result = systemService.updateAlreadyExistRoom(updated);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @Operation(summary = "Partially updates a room with the provided details", description = "Updates specified fields of the room with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully partially updated the room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to update this room"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(RestApiRoutes.UPDATE_ROOM)
    public ResponseEntity<PartialUpdateRoomOutput> updateRoomPartially(@PathVariable("roomId") String roomId, @Valid @RequestBody PartialUpdateRoomInput input) {

        PartialUpdateRoomInput updated = input.toBuilder().roomId(roomId).build();
        PartialUpdateRoomOutput result = systemService.partialUpdateRoom(updated);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Operation(summary = "Deletes room by specific ID", description = "Deletes a reservation for a specified room and date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to delete this reservation"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(RestApiRoutes.REMOVE_ROOM)
    public ResponseEntity<DeleteRoomOutput> deleteRooms(@PathVariable String roomId) {

        DeleteRoomInput roomForDelete = DeleteRoomInput.builder()
                .roomId(roomId)
                .build();

        DeleteRoomOutput result =systemService.deleteRooms(roomForDelete);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
