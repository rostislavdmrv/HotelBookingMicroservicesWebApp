package com.tinqinacademy.myhotel.core.services;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedService {
    Bed save (Bed entity);                   // C
    Optional<Bed> findById(UUID id);       // R
    Bed update (Bed entity);                 // U
    void deleteById(UUID id);            // D
    List<Bed> findAll();
}
