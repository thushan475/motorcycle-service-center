package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.InventoryAdjustmentRequestDTO;
import com.thushan.motorcycleservice.dto.request.InventoryRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.InventoryResponseDTO;
import com.thushan.motorcycleservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public CommonResponse<InventoryResponseDTO> createInventory(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO inventory = inventoryService.createInventory(request);
        return new CommonResponse<>(201, "Inventory record created successfully", inventory);
    }

    @GetMapping
    public CommonResponse<List<InventoryResponseDTO>> getAllInventory() {
        List<InventoryResponseDTO> inventory = inventoryService.getAllInventory();
        return new CommonResponse<>(200, "Inventory retrieved successfully", inventory);
    }

    @GetMapping("/low-stock")
    public CommonResponse<List<InventoryResponseDTO>> getLowStock(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {
        List<InventoryResponseDTO> inventory = inventoryService.getLowStock(threshold);
        return new CommonResponse<>(200, "Low stock inventory retrieved successfully", inventory);
    }

    @GetMapping("/value")
    public CommonResponse<Map<String, BigDecimal>> getTotalInventoryValue() {
        Map<String, BigDecimal> value = inventoryService.getTotalInventoryValue();
        return new CommonResponse<>(200, "Total inventory value calculated successfully", value);
    }

    @GetMapping("/{id}")
    public CommonResponse<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);
        return new CommonResponse<>(200, "Inventory retrieved successfully", inventory);
    }

    @PatchMapping("/{id}/adjust")
    public CommonResponse<InventoryResponseDTO> adjustStock(
            @PathVariable Long id, @Valid @RequestBody InventoryAdjustmentRequestDTO request) {
        InventoryResponseDTO inventory = inventoryService.adjustStock(id, request);
        return new CommonResponse<>(200, "Stock adjusted successfully", inventory);
    }

    @PutMapping("/{id}/quantity")
    public CommonResponse<InventoryResponseDTO> setQuantity(
            @PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        InventoryResponseDTO inventory = inventoryService.setQuantity(id, quantity);
        return new CommonResponse<>(200, "Inventory quantity updated successfully", inventory);
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return new CommonResponse<>(200, "Inventory record deleted successfully", null);
    }

    @DeleteMapping("/zero-quantity")
    public CommonResponse<Map<String, Integer>> deleteZeroQuantityRecords() {
        int deleted = inventoryService.deleteZeroQuantityRecords();
        return new CommonResponse<>(200, "Zero quantity inventory records deleted successfully", Map.of("deletedCount", deleted));
    }
}
