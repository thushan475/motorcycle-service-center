package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.ServiceOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderItemRepository extends JpaRepository<ServiceOrderItem, Long> {
    List<ServiceOrderItem> findByServiceOrderId(Long serviceOrderId);
}
