package com.tinqinacademy.myhotel.persistence.repositories;

import com.tinqinacademy.myhotel.persistence.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumberAndFirstNameAndLastName(String phoneNo, String firstName, String lastName);
    boolean existsByEmail(String email);

}
