package com.thushan.motorcycleservice.repository;

import com.thushan.motorcycleservice.entity.Invoice;
import com.thushan.motorcycleservice.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByCustomerId(Long customerId);
    boolean existsByServiceOrderId(Long serviceOrderId);

    @Query("SELECT i FROM Invoice i JOIN FETCH i.customer c JOIN FETCH i.serviceOrder so WHERE i.status IN :statuses ORDER BY i.invoiceDate DESC")
    List<Invoice> findByStatusIn(@Param("statuses") List<InvoiceStatus> statuses);

    @Modifying
    @Query("DELETE FROM Invoice i WHERE i.status = :status")
    int deleteByStatus(@Param("status") InvoiceStatus status);
}
