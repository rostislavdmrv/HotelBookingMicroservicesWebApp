package com.tinqinacademy.myhotel.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
;

@SpringBootApplication
@ComponentScan(basePackages = "com.tinqinacademy.myhotel")
public class MyHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyHotelApplication.class, args);
    }

}
