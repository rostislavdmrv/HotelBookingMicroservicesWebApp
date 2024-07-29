package com.tinqinacademy.myhotel.rest.controllers.hotel;


import com.tinqinacademy.myhotel.api.interfaces.room.RoomService;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.rest.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class RoomController {

    private final RoomService roomService;


    @Operation(summary = "Checks for available room for certain period", description = "Checks the availability of a room based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room availability retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to check for available this  room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(RestApiRoutes.CHECK_AVAILABILITY)
    public ResponseEntity<RoomOutput> isRoomAvailable(
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

        RoomOutput result = roomService.getAvailableRooms(input);

        return new ResponseEntity<>(result, HttpStatus.OK);


    }


    @Operation(summary = "Retrieves basic information for the specified room by specific ID", description = "Retrieves basic information for the specified room on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved information successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to view this room's information"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(RestApiRoutes.RETRIEVE_BASIC_INFO)
    public ResponseEntity<BasicInfoRoomOutput> infoForRoom(@PathVariable String roomId) {

        BasicInfoRoomInput inputId = BasicInfoRoomInput.builder().roomId(roomId).build();

        BasicInfoRoomOutput result = roomService.getInfoForRoom(inputId);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @Operation(summary = "Books a new room with the provided details", description = "Reserves a specified room for a user within the provided information, ensuring the room is available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked a room"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to book this room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PostMapping(RestApiRoutes.BOOK_ROOM)
    public ResponseEntity<BookRoomOutput> bookRoom(@PathVariable String roomId , @Valid @RequestBody BookRoomInput input) {

        BookRoomInput updatedInput = input.toBuilder()
                .roomId(roomId)
                .build();

        BookRoomOutput result = roomService.bookRoom(updatedInput);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }


    @Operation(summary = "Remove room reservation by specific ID", description = "Cancels a reservation for a specified room and date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to cancel this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping(RestApiRoutes.DELETE_RESERVATION)
    public ResponseEntity<UnbookRoomOutput> removeBookedRoom(@PathVariable String bookingId) {

        UnbookRoomInput unbookRoomInput = UnbookRoomInput.builder()
                .bookingId(bookingId)
                .build();

        UnbookRoomOutput result = roomService.removeBookedRoom(unbookRoomInput);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }




}
