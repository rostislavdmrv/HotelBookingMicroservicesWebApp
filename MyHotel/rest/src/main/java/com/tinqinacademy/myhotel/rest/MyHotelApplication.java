package com.tinqinacademy.myhotel.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
;


@OpenAPIDefinition(
        info = @Info(
                title = "Horizon",
                description = "The REST API of my hotel -- Horizon -- "
        )
)
@SpringBootApplication
@ComponentScan(basePackages = "com.tinqinacademy.myhotel")
@EntityScan(basePackages = "com.tinqinacademy.myhotel.persistence.models")
@EnableJpaRepositories(basePackages = "com.tinqinacademy.myhotel.persistence.repositories")
public class MyHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyHotelApplication.class, args);
    }

}
