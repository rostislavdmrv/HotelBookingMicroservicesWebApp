package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.User;

import java.util.Optional;

public interface UserRepository  extends GenericRepository<User>{
    Optional<User> findByEmail(String email);
}
