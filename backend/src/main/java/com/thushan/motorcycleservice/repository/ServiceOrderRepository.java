package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.ServiceOrder;
import com.thushan.motorcycleservice.entity.ServiceOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByCustomerId(Long customerId);

    @Query("SELECT so FROM ServiceOrder so JOIN FETCH so.customer c JOIN FETCH so.motorcycle m WHERE so.status = :status ORDER BY so.orderDate DESC")
    List<ServiceOrder> findByStatusWithDetails(@Param("status") ServiceOrderStatus status);

    @Query("SELECT DISTINCT so FROM ServiceOrder so JOIN FETCH so.customer c JOIN FETCH so.motorcycle m LEFT JOIN FETCH so.items WHERE so.customer.id = :customerId ORDER BY so.orderDate DESC")
    List<ServiceOrder> findServiceHistoryByCustomerId(@Param("customerId") Long customerId);

    @Modifying
    @Query("UPDATE ServiceOrder so SET so.status = :status WHERE so.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") ServiceOrderStatus status);

    @Modifying
    @Query("DELETE FROM ServiceOrder so WHERE so.status = :status")
    int deleteByStatus(@Param("status") ServiceOrderStatus status);
}
