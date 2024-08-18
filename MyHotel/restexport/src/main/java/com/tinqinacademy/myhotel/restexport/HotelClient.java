package com.tinqinacademy.myhotel.restexport;


import com.tinqinacademy.myhotel.api.feignclientapiroutes.FeignClientApiRoutes;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOutput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


import java.time.LocalDate;

@Headers({"Content-Type: application/json"})
public interface HotelClient {

    @RequestLine(FeignClientApiRoutes.CHECK_AVAILABILITY)
    RoomOutput isRoomAvailable(@Param LocalDate start, @Param LocalDate end,@Param Integer bedCount, @Param String bathroomType, @Param String bedSize);

    @RequestLine(FeignClientApiRoutes.RETRIEVE_BASIC_INFO)
    BasicInfoRoomOutput infoForRoom(@Param String roomId);

    @RequestLine(FeignClientApiRoutes.BOOK_ROOM)
    BasicInfoRoomOutput bookRoom(@Param String roomId, BookRoomInput input);

    @RequestLine(FeignClientApiRoutes.DELETE_RESERVATION)
    UnbookRoomOutput removeBookedRoom(@Param String bookingId);

    @RequestLine(FeignClientApiRoutes.REGISTER_GUEST)
    RegisterVisitorOutput  registerVisitor(RegisterVisitorInput input);

    @RequestLine(FeignClientApiRoutes.REPORT_VISITORS)
    ReportOutput getReport(@Param LocalDate startDate,
                           @Param LocalDate endDate,
                           @Param String firstName,
                           @Param String lastName,
                           @Param String phoneNo,
                           @Param String idCardNo,
                           @Param LocalDate idCardValidity,
                           @Param String idCardIssueAuthority,
                           @Param LocalDate idCardIssueDate,
                           @Param String roomNo);


    @RequestLine(FeignClientApiRoutes.CREATE_ROOM)
    CreateRoomOutput createRoom(CreateRoomInput input);

    @RequestLine(FeignClientApiRoutes.UPDATE_ROOM)
    UpdateRoomOutput updateAlreadyCreatedRoomInSystem(@Param String roomId, UpdateRoomInput input);

    @RequestLine(FeignClientApiRoutes.PART_UPDATE_ROOM)
    PartialUpdateRoomOutput updateRoomPartially(@Param String roomId, PartialUpdateRoomInput input);

    @RequestLine(FeignClientApiRoutes.DELETE_ROOM_BY_ADMIN)
    DeleteRoomOutput deleteRooms(@Param String roomId);





}
