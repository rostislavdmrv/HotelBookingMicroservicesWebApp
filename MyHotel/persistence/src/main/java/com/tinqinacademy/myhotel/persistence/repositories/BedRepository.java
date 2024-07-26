package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {
    Optional<Bed> findByBedSize(BedSize bedSize);
}
