package com.tinqinacademy.myhotel.api.interfaces.room;


import com.tinqinacademy.myhotel.api.operations.removesroomreservation.RoomForRemoveOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.removesroomreservation.RoomForRemoveInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

public interface RoomService {


    RoomOutput getAvailableRooms(RoomInput input);
    BasicInfoRoomOutput getInfoForRoom(BasicInfoRoomInput input);
    BookRoomOutput bookRoom(BookRoomInput input);
    RoomForRemoveOutput removeBookedRoom(RoomForRemoveInput input);

}
