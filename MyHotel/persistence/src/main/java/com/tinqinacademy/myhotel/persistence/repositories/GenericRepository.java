package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.Room;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface GenericRepository <T extends Entity> {


    T save (T entity);                   // C
    Optional<T> findById(UUID id);       // R
    T update (T entity);                 // U
    void deleteById(UUID id);            // D
    List<T> findAll();
}
