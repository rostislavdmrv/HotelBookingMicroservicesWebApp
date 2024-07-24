package com.tinqinacademy.myhotel.api.interfaces.room;


import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.UnbookRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

public interface RoomService {


    RoomOutput getAvailableRooms(RoomInput input);
    BasicInfoRoomOutput getInfoForRoom(BasicInfoRoomInput input);
    BookRoomOutput bookRoom(BookRoomInput input);
    UnbookRoomOutput removeBookedRoom(UnbookRoomInput input);

}
