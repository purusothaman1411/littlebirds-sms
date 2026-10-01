package com.littlebirds.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.littlebirds.sms.entity.Role;
import com.littlebirds.sms.entity.StaffUser;

public interface StaffUserRepository extends JpaRepository<StaffUser, String> {

    Optional<StaffUser> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(Role role);
}
