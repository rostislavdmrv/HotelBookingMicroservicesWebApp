package com.tinqinacademy.myhotel.core.converters;


import com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin.CreateRoomInput;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import org.springframework.core.convert.converter.Converter;

public class CreateRoomInputToRoomConverter implements Converter<CreateRoomInput, Room> {
    @Override
    public Room convert(CreateRoomInput source) {

        return null;
    }
}
