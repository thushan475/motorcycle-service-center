package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {
    Optional<Motorcycle> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Motorcycle> findByCustomerId(Long customerId);
}
