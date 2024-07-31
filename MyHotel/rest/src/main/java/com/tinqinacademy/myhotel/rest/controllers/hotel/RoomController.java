package com.tinqinacademy.myhotel.rest.controllers.hotel;


import com.tinqinacademy.myhotel.api.interfaces.room.RoomService;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOperation;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOperation;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.IsRoomFreeOperation;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOperation;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOperation;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Room REST APIs")
public class RoomController extends BaseController {

    private final BasicInfoRoomOperation basicInfoRoomOperation;
    private final BookRoomOperation bookRoomOperation;
    private final IsRoomFreeOperation isRoomFreeOperation;
    private final UnbookRoomOperation unbookRoomOperation;



    @Operation(summary = "Checks for available room for certain period", description = "Checks the availability of a room based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room availability retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to check for available this  room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(RestApiRoutes.CHECK_AVAILABILITY)
    public ResponseEntity<?> isRoomAvailable(
             @RequestParam(required = false) LocalDate start,
             @RequestParam(required = false) LocalDate end,
             @RequestParam(required = false) Integer bedCount,
             @RequestParam(required = false) String bathroomType,
             @RequestParam(required = false) String bedSize) {


        RoomInput input = RoomInput.builder()
                .startDate(start)
                .endDate(end)
                .bedCount(bedCount)
                .bedSize(bedSize)
                .bathroomType(bathroomType)
                .build();

        Either<ErrorWrapper,RoomOutput> result = isRoomFreeOperation.process(input);

        return handle(result);


    }

    @Operation(summary = "Retrieves basic information for the specified room by specific ID", description = "Retrieves basic information for the specified room on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved information successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to view this room's information"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(RestApiRoutes.RETRIEVE_BASIC_INFO)
    public ResponseEntity<?> infoForRoom(@PathVariable String roomId) {

        BasicInfoRoomInput input = BasicInfoRoomInput.builder()
                .roomId(roomId)
                .build();

         Either<ErrorWrapper,BasicInfoRoomOutput> result = basicInfoRoomOperation.process(input);
        return handle(result);

    }


    @Operation(summary = "Books a new room with the provided details", description = "Reserves a specified room for a user within the provided information, ensuring the room is available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked a room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to book this room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PostMapping(RestApiRoutes.BOOK_ROOM)
    public ResponseEntity<?> bookRoom(@PathVariable String roomId ,@RequestBody BookRoomInput input) {

        BookRoomInput updatedInput = input.toBuilder()
                .roomId(roomId)
                .build();

        Either<ErrorWrapper,BookRoomOutput> result = bookRoomOperation.process(updatedInput);
        return handleWithStatus(result, HttpStatus.CREATED);

    }

    @Operation(summary = "Remove room reservation by specific ID", description = "Cancels a reservation for a specified room and date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to cancel this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping(RestApiRoutes.DELETE_RESERVATION)
    public ResponseEntity<?> removeBookedRoom(@PathVariable String bookingId) {

        UnbookRoomInput unbookRoomInput = UnbookRoomInput.builder()
                .bookingId(bookingId)
                .build();

        Either<ErrorWrapper,UnbookRoomOutput> result = unbookRoomOperation.process(unbookRoomInput);
        return handle(result);

    }




}
