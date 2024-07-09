package com.tinqinacademy.myhotel.services.system.impl;

import com.tinqinacademy.myhotel.exceptions.TestException;
import com.tinqinacademy.myhotel.models.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.models.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.models.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.models.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies.RoomRenterOutput;
import com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyInput;
import com.tinqinacademy.myhotel.models.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies.RoomRenterOccupancyOutput;
import com.tinqinacademy.myhotel.models.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.models.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.models.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.models.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.models.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import com.tinqinacademy.myhotel.services.system.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    @Override
    public RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input) {
        log.info("Starts registering visitor as new renter {}", input);
        RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();
        log.info("End : Successfully registered {}", input);
        return output;
    }

    @Override
    public RoomRenterOccupancyOutput getRoomRentersOccupancies(RoomRenterOccupancyInput input) {
        log.info("Starts getting room renters occupancy {}", input);
        RoomRenterOccupancyOutput output = RoomRenterOccupancyOutput.builder().roomRenters((List<RoomRenterOutput>) input).build();
        log.info("End : Successfully getting room renters occupancy {}", input);
        return output;
    }

    @Override
    public CreateRoomOutput createNewRoom(CreateRoomInput input) {
        UUID uuid = UUID.randomUUID();
        int num = 5;
        log.info("Starts creating new room {}", input);
        CreateRoomOutput output = CreateRoomOutput.builder().roomId(uuid.toString()).build();
        log.info("End : Successfully creating new room with ID {}", output);

        if (true){
            throw new TestException("Error");
        }

        return output;
    }

    @Override
    public UpdateRoomOutput updateAlreadyExistRoom(String roomId,UpdateRoomInput input) {
        log.info("Starts updating existing room {}", input);
        UpdateRoomOutput output = UpdateRoomOutput.builder().roomId(roomId).build();
        log.info("End : Successfully updating existing room with ID {}", output);
        return output;
    }

    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(String roomId,PartialUpdateRoomInput input) {


        log.info("Starts partially updating room with ID '{}'and details {}", roomId, input);
        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder().roomId(roomId).build();

        log.info("End : Successfully partially updating room with ID {}", output);
        return output;
    }

    @Override
    public DeleteRoomOutput deleteRooms(DeleteRoomInput input) {
        log.info("Starts deleting rooms {}", input);
        DeleteRoomOutput output = DeleteRoomOutput.builder().build();
        log.info("End : Successfully deleting rooms {}", output);
        return output;
    }


}
