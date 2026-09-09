package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.MotorcycleBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorcycleBrandRepository extends JpaRepository<MotorcycleBrand, Long> {
    boolean existsByName(String name);
}
