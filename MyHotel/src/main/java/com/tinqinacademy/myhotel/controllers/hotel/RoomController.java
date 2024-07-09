package com.tinqinacademy.myhotel.controllers.hotel;

import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomInput;
import com.tinqinacademy.myhotel.services.hotel.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/hotel")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Checks for available room for certain period", description = "Checks the availability of a room based on the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room availability retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You don't have permission to check for available this  room"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rooms")
    public ResponseEntity<RoomOutput> isRoomAvailable(
             @RequestParam LocalDate start,
             @RequestParam LocalDate end,
             @RequestParam Integer bedCount,
             @RequestParam String bathroomType,
             @RequestParam String bedSize) {

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
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomId}")
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
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{roomId}")
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
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<RoomForRemoveOutput> removeBookedRoom(@PathVariable String bookingId) {

        RoomForRemoveInput roomForRemoveInput = RoomForRemoveInput.builder()
                .roomId(bookingId)
                .build();

        RoomForRemoveOutput result = roomService.removeBookedRoom(roomForRemoveInput);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


}