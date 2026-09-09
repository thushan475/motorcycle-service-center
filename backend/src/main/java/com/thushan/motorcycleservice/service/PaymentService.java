package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.PaymentRequestDTO;
import com.thushan.motorcycleservice.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO request);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO getPaymentById(Long id);
    List<PaymentResponseDTO> getPaymentsByInvoice(Long invoiceId);
}
