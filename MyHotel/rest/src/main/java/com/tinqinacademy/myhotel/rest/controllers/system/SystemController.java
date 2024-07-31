package com.tinqinacademy.myhotel.rest.controllers.system;

import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOperation;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOperation;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOperation;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOperation;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.api.interfaces.system.SystemService;
import com.tinqinacademy.myhotel.rest.controllers.BaseController;
import com.tinqinacademy.myhotel.rest.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "System REST APIs")
public class SystemController extends BaseController {

    private  final SystemService systemService;
    private final CreateRoomOperation createRoomOperation;
    private final DeleteRoomOperation deleteRoomOperation;
    private final RegisterVisitorOperation registerVisitorOperation;
    private final ReportOperation reportOperation;
    private final UpdateRoomOperation updateRoomOperation;


    @Operation(summary = "Registers a new renter ", description = "Registers a new renter in the system with specific details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered renter"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to register a new renter")

    })
    @PostMapping(RestApiRoutes.REGISTER_NEW_GUEST)
    public ResponseEntity<?> registerVisitorAsRenter(@RequestBody RegisterVisitorInput input) {

        Either<ErrorWrapper,RegisterVisitorOutput>  result = registerVisitorOperation.process(input);
        return handleWithStatus(result,HttpStatus.CREATED);

    }


    @Operation(summary = "Creates a report based on specified criteria", description = "Creates a detailed report by evaluating various criteria related to room occupancies and visitor information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to generate the report")

    })
    @GetMapping(RestApiRoutes.REPORT_VISITORS)
    public ResponseEntity<?> getVisitorReports(
             @RequestParam(required = false) LocalDate startDate,
             @RequestParam(required = false) LocalDate endDate,
             @RequestParam(required = false) String firstName,
             @RequestParam(required = false) String lastName,
             @RequestParam(required = false) String phoneNo,
             @RequestParam(required = false) String idCardNo,
             @RequestParam(required = false) LocalDate idCardValidity,
             @RequestParam(required = false) String idCardIssueAuthority,
             @RequestParam(required = false) LocalDate idCardIssueDate,
             @RequestParam(required = false) String roomNo) {

        ReportInput input = ReportInput.builder()
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


        Either<ErrorWrapper,ReportOutput> result = reportOperation.process(input);
        return handle(result);

    }

    @Operation(summary = "Creates a new room with the provided details", description = "Creates a room with specific parameters that will later serve as a template to save a room from a user ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully create a room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to create this room")
    })
    @PostMapping(RestApiRoutes.CREATE_ROOM)
    public ResponseEntity<?> createNewRoomInSystem(@RequestBody CreateRoomInput input) {

        Either<ErrorWrapper,CreateRoomOutput> result = createRoomOperation.process(input);
        return handle(result);

    }

    @Operation(summary = "Updates a room with the provided details", description = "Updates the specified room with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to update this room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PutMapping(RestApiRoutes.UPDATE_ROOM)
    public ResponseEntity<?> updateAlreadyCreatedRoomInSystem(@PathVariable("roomId") String roomId, @RequestBody @Valid UpdateRoomInput input) {


        UpdateRoomInput updated = input.toBuilder().roomId(roomId).build();
        Either<ErrorWrapper,UpdateRoomOutput> result = updateRoomOperation.process(updated);
        return handle(result);

    }

    @Operation(summary = "Partially updates a room with the provided details", description = "Updates specified fields of the room with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully partially updated the room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to update this room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PatchMapping(value=RestApiRoutes.PART_UPDATE_ROOM,consumes = "application/json-patch+json")
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
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping(RestApiRoutes.REMOVE_ROOM)
    public ResponseEntity<?> deleteRooms(@PathVariable String roomId) {

        DeleteRoomInput roomForDelete = DeleteRoomInput.builder()
                .roomId(roomId)
                .build();

        Either<ErrorWrapper,DeleteRoomOutput> result =deleteRoomOperation.process(roomForDelete);
        return handle(result);

    }
}
