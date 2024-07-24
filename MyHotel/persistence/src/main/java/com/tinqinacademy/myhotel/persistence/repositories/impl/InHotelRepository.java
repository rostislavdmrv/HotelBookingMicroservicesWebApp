package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.persistence.repositories.HotelRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class InHotelRepository implements HotelRepository, BeanNameAware,
        InitializingBean, DisposableBean {
    private String name;

    public InHotelRepository() {
        System.out.println("Instantiating... ");
    }


    private final List<RoomInput> allRooms = List.of(

    );

    @Override
    public List<RoomInput> getAllRooms() {
        return allRooms;
    }


    @Override
    public void destroy() throws Exception {
        System.out.println("Clean up. Bye!");
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this.name + " manages " + getAllRooms().size() + " rooms.");
    }


}
