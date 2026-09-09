package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.InventoryAdjustmentRequestDTO;
import com.thushan.motorcycleservice.dto.request.InventoryRequestDTO;
import com.thushan.motorcycleservice.dto.response.InventoryResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InventoryService {
    InventoryResponseDTO createInventory(InventoryRequestDTO request);
    List<InventoryResponseDTO> getAllInventory();
    InventoryResponseDTO getInventoryById(Long id);
    InventoryResponseDTO adjustStock(Long id, InventoryAdjustmentRequestDTO request);
    void deleteInventory(Long id);
    List<InventoryResponseDTO> getLowStock(Integer threshold);
    Map<String, BigDecimal> getTotalInventoryValue();
    InventoryResponseDTO setQuantity(Long id, Integer quantity);
    int deleteZeroQuantityRecords();
}
