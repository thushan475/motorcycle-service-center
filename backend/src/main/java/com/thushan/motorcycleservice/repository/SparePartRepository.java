package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    Optional<SparePart> findByPartNumber(String partNumber);
    boolean existsByPartNumber(String partNumber);
}
