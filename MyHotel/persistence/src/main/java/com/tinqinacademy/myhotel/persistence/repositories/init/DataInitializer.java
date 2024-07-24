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

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        log.info("Start DataInitializer for beds");

        // I get all the beds
        List<Bed> existingBeds = bedRepository.findAll();
        Set<String> dbBedCodes = jdbcTemplate.query(
                "SELECT bed_size FROM beds",
                (ResultSet rs) -> {
                    Set<String> codes = new HashSet<>();
                    while (rs.next()) {
                        codes.add(rs.getString("bed_size"));
                    }
                    return codes;
                }
        );



        // this is for inserting or updating a bed
        for (BedSize bedSize : BedSize.values()) {
            //if its empty we skip it
            if (bedSize.getCode().isEmpty()) {
                continue;
            }
            //I get the bed that matches the bed size
            //if it exists it will be in the optional
            //if not it will be added to the database
            Optional<Bed> matchingBed = existingBeds.stream()
                    .filter(b -> b.getBedSize() == bedSize)
                    .findFirst();

            if (matchingBed.isPresent()) {
                // Update capacity if different
                if (!matchingBed.get().getCapacity().equals(bedSize.getCapacity())) {
                    jdbcTemplate.update(
                            "UPDATE beds SET capacity = ? WHERE bed_size = ?::bed_size",
                            bedSize.getCapacity(),
                            bedSize.toString()
                    );
                    log.info("Updated capacity for bedSize = {}", bedSize);
                }
            } else {
                // Insert new bed record
                Bed bed = Bed.builder()
                        .bedSize(bedSize)
                        .capacity(bedSize.getCapacity())
                        .build();
                bedRepository.save(bed);

                log.info("Adding bed = {}", bed);
            }

            if (dbBedCodes != null)
                dbBedCodes.remove(bedSize.toString());
        }

        if (dbBedCodes != null){
            for (String code : dbBedCodes) {
                jdbcTemplate.update("DELETE FROM beds WHERE bed_size = ?", code);
                log.info("Deleted bed with size code = {}", code);
            }
        }



        log.info("End DataInitializer for beds");
    }
}
