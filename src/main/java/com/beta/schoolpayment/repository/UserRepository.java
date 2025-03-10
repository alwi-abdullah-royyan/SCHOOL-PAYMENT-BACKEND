package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByNis(Long nis);
    Optional<User> findUserByEmail(String email);
    Optional<User> findByName(String name);
}
