package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySparePartId(Long sparePartId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.sparePart sp WHERE i.quantity <= :threshold ORDER BY i.quantity ASC")
    List<Inventory> findLowStock(@Param("threshold") Integer threshold);

    @Query(value = "SELECT COALESCE(SUM(i.quantity * sp.selling_price), 0) FROM inventory i INNER JOIN spare_parts sp ON i.spare_part_id = sp.id", nativeQuery = true)
    BigDecimal calculateTotalInventoryValue();

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = :quantity WHERE i.id = :id")
    int updateQuantityById(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.quantity = 0")
    int deleteZeroQuantityRecords();
}
