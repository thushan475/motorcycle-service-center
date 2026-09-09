package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.PaymentRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.PaymentResponseDTO;
import com.thushan.motorcycleservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO payment = paymentService.createPayment(request);
        return new CommonResponse<>(201, "Payment recorded successfully", payment);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return new CommonResponse<>(200, "Payments retrieved successfully", payments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return new CommonResponse<>(200, "Payment retrieved successfully", payment);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<PaymentResponseDTO>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return new CommonResponse<>(200, "Payments retrieved successfully", payments);
    }
}