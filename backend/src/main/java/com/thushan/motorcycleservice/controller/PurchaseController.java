package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.PurchaseRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.PurchaseResponseDTO;
import com.thushan.motorcycleservice.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public CommonResponse<PurchaseResponseDTO> createPurchase(@Valid @RequestBody PurchaseRequestDTO request) {
        PurchaseResponseDTO purchase = purchaseService.createPurchase(request);
        return new CommonResponse<>(201, "Purchase created successfully", purchase);
    }

    @GetMapping
    public CommonResponse<List<PurchaseResponseDTO>> getAllPurchases() {
        List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchases();
        return new CommonResponse<>(200, "Purchases retrieved successfully", purchases);
    }

    @GetMapping("/{id}")
    public CommonResponse<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id) {
        PurchaseResponseDTO purchase = purchaseService.getPurchaseById(id);
        return new CommonResponse<>(200, "Purchase retrieved successfully", purchase);
    }

    @PatchMapping("/{id}/status")
    public CommonResponse<PurchaseResponseDTO> updatePurchaseStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        PurchaseResponseDTO purchase = purchaseService.updatePurchaseStatus(id, body.get("status"));
        return new CommonResponse<>(200, "Purchase status updated successfully", purchase);
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return new CommonResponse<>(200, "Purchase deleted successfully", null);
    }
}
