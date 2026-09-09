package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.InventoryAdjustmentRequestDTO;
import com.thushan.motorcycleservice.dto.request.InventoryRequestDTO;
import com.thushan.motorcycleservice.dto.response.InventoryResponseDTO;
import com.thushan.motorcycleservice.entity.Inventory;
import com.thushan.motorcycleservice.entity.SparePart;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.InventoryRepository;
import com.thushan.motorcycleservice.repository.SparePartRepository;
import com.thushan.motorcycleservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final SparePartRepository sparePartRepository;

    @Override
    public InventoryResponseDTO createInventory(InventoryRequestDTO request) {
        log.info("Execute createInventory()");
        try {
            SparePart sparePart = sparePartRepository.findById(request.getSparePartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + request.getSparePartId()));

            if (inventoryRepository.findBySparePartId(sparePart.getId()).isPresent()) {
                throw new DuplicateResourceException("Inventory record already exists for this spare part");
            }

            if (request.getQuantity() < 0) {
                throw new BadRequestException("Stock quantity cannot be negative");
            }

            Inventory inventory = Inventory.builder()
                    .quantity(request.getQuantity())
                    .sparePart(sparePart)
                    .build();

            Inventory saved = inventoryRepository.save(inventory);
            log.info("Inventory created for spare part id: {} with quantity: {}", sparePart.getId(), saved.getQuantity());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createInventory() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<InventoryResponseDTO> getAllInventory() {
        log.info("Execute getAllInventory()");
        try {
            return inventoryRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllInventory() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InventoryResponseDTO getInventoryById(Long id) {
        log.info("Execute getInventoryById()");
        try {
            return toDto(findInventoryOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getInventoryById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InventoryResponseDTO adjustStock(Long id, InventoryAdjustmentRequestDTO request) {
        log.info("Execute adjustStock()");
        try {
            Inventory inventory = findInventoryOrThrow(id);

            int newQuantity = inventory.getQuantity() + request.getChange();
            if (newQuantity < 0) {
                throw new BadRequestException("Stock cannot become negative. Current stock: "
                        + inventory.getQuantity() + ", requested change: " + request.getChange());
            }

            inventory.setQuantity(newQuantity);
            Inventory updated = inventoryRepository.save(inventory);
            log.info("Inventory id: {} stock adjusted by {} -> new quantity: {}", id, request.getChange(), newQuantity);
            return toDto(updated);

        } catch (Exception e) {
            log.error("Error in adjustStock() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteInventory(Long id) {
        log.info("Execute deleteInventory()");
        try {
            Inventory inventory = findInventoryOrThrow(id);
            inventoryRepository.delete(inventory);
            log.info("Inventory deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteInventory() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<InventoryResponseDTO> getLowStock(Integer threshold) {
        log.info("Execute getLowStock() threshold={}", threshold);
        try {
            if (threshold == null || threshold < 0) {
                threshold = 10;
            }
            return inventoryRepository.findLowStock(threshold).stream().map(this::toDto).toList();
        } catch (Exception e) {
            log.error("Error in getLowStock() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, BigDecimal> getTotalInventoryValue() {
        log.info("Execute getTotalInventoryValue()");
        try {
            BigDecimal total = inventoryRepository.calculateTotalInventoryValue();
            if (total == null) {
                total = BigDecimal.ZERO;
            }
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("totalInventoryValue", total);
            return result;
        } catch (Exception e) {
            log.error("Error in getTotalInventoryValue() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public InventoryResponseDTO setQuantity(Long id, Integer quantity) {
        log.info("Execute setQuantity() id={} quantity={}", id, quantity);
        try {
            if (quantity == null || quantity < 0) {
                throw new BadRequestException("Quantity cannot be null or negative");
            }
            Inventory inventory = findInventoryOrThrow(id);
            int updated = inventoryRepository.updateQuantityById(id, quantity);
            if (updated == 0) {
                throw new ResourceNotFoundException("Inventory record not found with id: " + id);
            }
            inventory.setQuantity(quantity);
            return toDto(inventory);
        } catch (Exception e) {
            log.error("Error in setQuantity() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public int deleteZeroQuantityRecords() {
        log.info("Execute deleteZeroQuantityRecords()");
        try {
            return inventoryRepository.deleteZeroQuantityRecords();
        } catch (Exception e) {
            log.error("Error in deleteZeroQuantityRecords() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Inventory findInventoryOrThrow(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with id: " + id));
    }

    private InventoryResponseDTO toDto(Inventory inventory) {
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getQuantity(),
                inventory.getLastUpdated(),
                inventory.getSparePart().getId(),
                inventory.getSparePart().getName(),
                inventory.getSparePart().getPartNumber()
        );
    }
}
