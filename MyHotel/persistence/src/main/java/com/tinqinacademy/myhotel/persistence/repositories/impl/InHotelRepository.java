package com.tinqinacademy.myhotel.persistence.repositories.impl;

import com.tinqinacademy.myhotel.api.operations.isroomavailable.RoomInput;
import com.tinqinacademy.myhotel.persistence.repositories.HotelRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class InHotelRepository implements HotelRepository, BeanNameAware,
        InitializingBean, DisposableBean {
    private String name;

    public InHotelRepository() {
        System.out.println("Instantiating... ");
    }


    private final List<RoomInput> allRooms = List.of(
            RoomInput.builder()
                    .id("1")
                    .startDate(LocalDate.of(2024, 7, 1))
                    .endDate(LocalDate.of(2024, 7, 10))
                    .bedCount(2)
                    .bathroomType("private")
                    .bedSize("king_size")
                    .build(),

            RoomInput.builder()
                    .id("2")
                    .startDate(LocalDate.of(2024, 7, 5))
                    .endDate(LocalDate.of(2024, 7, 15))
                    .bedCount(2)
                    .bathroomType("shared")
                    .bedSize("queen")
                    .build(),

            RoomInput.builder()
                    .id("3")
                    .startDate(LocalDate.of(2024, 7, 10))
                    .endDate(LocalDate.of(2024, 7, 20))
                    .bedCount(1)
                    .bathroomType("private")
                    .bedSize("small_double")
                    .build(),

            RoomInput.builder()
                    .id("4")
                    .startDate(LocalDate.of(2024, 7, 15))
                    .endDate(LocalDate.of(2024, 7, 25))
                    .bedCount(3)
                    .bathroomType("shared")
                    .bedSize("double")
                    .build(),

            RoomInput.builder()
                    .id("5")
                    .startDate(LocalDate.of(2024, 7, 20))
                    .endDate(LocalDate.of(2024, 7, 30))
                    .bedCount(2)
                    .bathroomType("private")
                    .bedSize("single")
                    .build()
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
