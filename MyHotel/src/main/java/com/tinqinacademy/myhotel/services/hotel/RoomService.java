package com.tinqinacademy.myhotel.services.hotel;


import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomOutput;
import com.tinqinacademy.myhotel.models.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.models.operations.removesroomreservation.RoomForRemoveInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomOutput;
import com.tinqinacademy.myhotel.models.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.models.operations.returnsbasicinfoforroom.BasicInfoRoomInput;

public interface RoomService {


    RoomOutput getAvailableRooms(RoomInput input);
    BasicInfoRoomOutput getInfoForRoom(BasicInfoRoomInput input);
    BookRoomOutput bookRoom(BookRoomInput input);
    RoomForRemoveOutput removeBookedRoom(RoomForRemoveInput input);

}
