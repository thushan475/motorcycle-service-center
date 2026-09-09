package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.PurchaseRequestDTO;
import com.thushan.motorcycleservice.dto.response.PurchaseResponseDTO;
import com.thushan.motorcycleservice.entity.Purchase;
import com.thushan.motorcycleservice.entity.PurchaseStatus;
import com.thushan.motorcycleservice.entity.Supplier;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.PurchaseRepository;
import com.thushan.motorcycleservice.repository.SupplierRepository;
import com.thushan.motorcycleservice.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO request) {
        log.info("Execute createPurchase()");
        try {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));

            Purchase purchase = Purchase.builder()
                    .purchaseDate(LocalDate.now())
                    .totalAmount(request.getTotalAmount())
                    .status(PurchaseStatus.PENDING)
                    .supplier(supplier)
                    .build();

            Purchase saved = purchaseRepository.save(purchase);
            log.info("Purchase created with id: {} for supplier id: {}", saved.getId(), supplier.getId());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createPurchase() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseResponseDTO> getAllPurchases() {
        log.info("Execute getAllPurchases()");
        try {
            return purchaseRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllPurchases() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PurchaseResponseDTO getPurchaseById(Long id) {
        log.info("Execute getPurchaseById()");
        try {
            return toDto(findPurchaseOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getPurchaseById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PurchaseResponseDTO updatePurchaseStatus(Long id, String status) {
        log.info("Execute updatePurchaseStatus()");
        try {
            Purchase purchase = findPurchaseOrThrow(id);

            PurchaseStatus newStatus;
            try {
                newStatus = PurchaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid purchase status: " + status
                        + ". Allowed values: PENDING, RECEIVED, CANCELLED");
            }

            purchase.setStatus(newStatus);
            Purchase updated = purchaseRepository.save(purchase);
            log.info("Purchase id: {} status changed to: {}", updated.getId(), newStatus);
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in updatePurchaseStatus() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deletePurchase(Long id) {
        log.info("Execute deletePurchase()");
        try {
            Purchase purchase = findPurchaseOrThrow(id);
            purchaseRepository.delete(purchase);
            log.info("Purchase deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deletePurchase() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Purchase findPurchaseOrThrow(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
    }

    private PurchaseResponseDTO toDto(Purchase purchase) {
        return new PurchaseResponseDTO(
                purchase.getId(),
                purchase.getPurchaseDate(),
                purchase.getTotalAmount(),
                purchase.getStatus().name(),
                purchase.getSupplier().getId(),
                purchase.getSupplier().getName()
        );
    }
}
