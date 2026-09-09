package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.request.InvoiceRequestDTO;
import com.thushan.motorcycleservice.dto.response.InvoiceDetailResponseDTO;
import com.thushan.motorcycleservice.dto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO request);
    List<InvoiceResponseDTO> getAllInvoices();
    InvoiceResponseDTO getInvoiceById(Long id);
    InvoiceDetailResponseDTO getInvoiceDetail(Long id);
    List<InvoiceResponseDTO> getInvoicesByCustomer(Long customerId);
    void deleteInvoice(Long id);
    boolean invoiceExistsForServiceOrder(Long serviceOrderId);
    List<InvoiceResponseDTO> getUnpaidInvoices();
    int deleteCancelledInvoices();
}
