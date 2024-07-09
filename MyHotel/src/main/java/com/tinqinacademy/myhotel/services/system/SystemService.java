package com.tinqinacademy.myhotel.services.system;

import com.tinqinacademy.myhotel.models.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.models.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.models.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.models.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyInput;
import com.tinqinacademy.myhotel.models.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyOutput;
import com.tinqinacademy.myhotel.models.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.models.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.models.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.models.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.models.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;

public interface SystemService {

    RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input);

    RoomRenterOccupancyOutput getRoomRentersOccupancies(RoomRenterOccupancyInput input);

    CreateRoomOutput createNewRoom(CreateRoomInput input);

    UpdateRoomOutput updateAlreadyExistRoom(String roomId,UpdateRoomInput input);

    PartialUpdateRoomOutput partialUpdateRoom(String roomId, PartialUpdateRoomInput input);

    DeleteRoomOutput deleteRooms(DeleteRoomInput input);
}
