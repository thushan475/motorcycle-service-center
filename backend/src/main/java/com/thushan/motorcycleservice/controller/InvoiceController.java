package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.dto.request.InvoiceRequestDTO;
import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.InvoiceDetailResponseDTO;
import com.thushan.motorcycleservice.dto.response.InvoiceResponseDTO;
import com.thushan.motorcycleservice.service.EmailService;
import com.thushan.motorcycleservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final EmailService emailService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        InvoiceResponseDTO invoice = invoiceService.createInvoice(request);
        return new CommonResponse<>(201, "Invoice created successfully", invoice);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<InvoiceResponseDTO>> getAllInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
        return new CommonResponse<>(200, "Invoices retrieved successfully", invoices);
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<InvoiceResponseDTO>> getUnpaidInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getUnpaidInvoices();
        return new CommonResponse<>(200, "Unpaid and partially paid invoices retrieved successfully", invoices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(id);
        return new CommonResponse<>(200, "Invoice retrieved successfully", invoice);
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<InvoiceDetailResponseDTO> getInvoiceDetail(@PathVariable Long id) {
        InvoiceDetailResponseDTO detail = invoiceService.getInvoiceDetail(id);
        return new CommonResponse<>(200, "Invoice detail retrieved successfully", detail);
    }

    @PostMapping("/{id}/send-ebill")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<Void> sendEbill(@PathVariable Long id) {
        emailService.sendInvoiceEbill(id);
        return new CommonResponse<>(200, "E-bill sent successfully", null);
    }



    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<List<InvoiceResponseDTO>> getInvoicesByCustomer(@PathVariable Long customerId) {
        List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByCustomer(customerId);
        return new CommonResponse<>(200, "Invoices retrieved successfully", invoices);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return new CommonResponse<>(200, "Invoice deleted successfully", null);
    }

    @DeleteMapping("/cancelled")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<Map<String, Integer>> deleteCancelledInvoices() {
        int deleted = invoiceService.deleteCancelledInvoices();
        return new CommonResponse<>(200, "Cancelled invoices deleted successfully", Map.of("deletedCount", deleted));
    }
}
