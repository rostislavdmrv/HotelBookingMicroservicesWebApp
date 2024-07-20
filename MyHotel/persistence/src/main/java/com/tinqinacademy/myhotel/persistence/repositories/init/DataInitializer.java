package com.tinqinacademy.myhotel.persistence.repositories.init;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class DataInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;
    private final BedRepository bedRepository;

    @Autowired
    public DataInitializer(JdbcTemplate jdbcTemplate, BedRepository bedRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bedRepository = bedRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Started DataInitializer for beds.");
        for (BedSize bedSize : BedSize.values()) {
            if (bedSize.getCode().isEmpty()) {
                continue;
            }
            try {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM beds WHERE bed = ?::bed_size",
                        Integer.class,
                        bedSize.toString()
                );
                if (count == null || count == 0) {
                    Bed bed = Bed.builder()
                            .id(UUID.randomUUID())
                            .bedSize(bedSize)
                            .capacity(bedSize.getCapacity())
                            .build();
                    bedRepository.save(bed);
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
                log.error(String.valueOf(e.getCause()));
            }





        }

        // TODO: update
        log.info("Ended DataInitializer for beds.");
    }
}
