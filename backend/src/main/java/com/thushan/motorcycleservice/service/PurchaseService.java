package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.PurchaseRequestDTO;
import com.thushan.motorcycleservice.dto.response.PurchaseResponseDTO;

import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO request);
    List<PurchaseResponseDTO> getAllPurchases();
    PurchaseResponseDTO getPurchaseById(Long id);
    PurchaseResponseDTO updatePurchaseStatus(Long id, String status);
    void deletePurchase(Long id);
}
