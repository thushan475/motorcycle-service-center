package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.ServiceOrderSparePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderSparePartRepository extends JpaRepository<ServiceOrderSparePart, Long> {
    List<ServiceOrderSparePart> findByServiceOrderId(Long serviceOrderId);
}
