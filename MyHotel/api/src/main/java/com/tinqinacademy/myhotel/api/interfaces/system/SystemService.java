package com.tinqinacademy.myhotel.api.interfaces.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomInput;
import com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin.DeleteRoomOutput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportInput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorInput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOutput;
import com.tinqinacademy.myhotel.api.operations.registersvisitors.RegisterVisitorOutput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin.UpdateRoomOutput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomInput;
import com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin.PartialUpdateRoomOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface SystemService {

    RegisterVisitorOutput registerVisitorAsRenter(RegisterVisitorInput input);

    ReportOutput reportByCriteria(ReportInput input);

    CreateRoomOutput createNewRoom(CreateRoomInput input);

    UpdateRoomOutput updateAlreadyExistRoom(UpdateRoomInput input);

    PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input) ;

    DeleteRoomOutput deleteRooms(DeleteRoomInput input);
}
