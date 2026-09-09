package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.PaymentRequestDTO;
import com.thushan.motorcycleservice.dto.response.PaymentResponseDTO;
import com.thushan.motorcycleservice.entity.Invoice;
import com.thushan.motorcycleservice.entity.InvoiceStatus;
import com.thushan.motorcycleservice.entity.Payment;
import com.thushan.motorcycleservice.entity.PaymentMethod;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.InvoiceRepository;
import com.thushan.motorcycleservice.repository.PaymentRepository;
import com.thushan.motorcycleservice.service.EmailService;
import com.thushan.motorcycleservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        log.info("Execute createPayment()");
        try {
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Payment amount must be greater than zero");
            }

            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

            if (invoice.getStatus() == InvoiceStatus.PAID) {
                throw new BadRequestException("This invoice is already fully paid");
            }
            if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
                throw new BadRequestException("Cannot pay a cancelled invoice");
            }

            PaymentMethod method;
            try {
                method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid payment method: " + request.getPaymentMethod()
                        + ". Allowed values: CASH, CARD, BANK_TRANSFER");
            }

            BigDecimal alreadyPaid = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal newTotalPaid = alreadyPaid.add(request.getAmount());

            if (newTotalPaid.compareTo(invoice.getTotalAmount()) > 0) {
                throw new BadRequestException("Payment amount exceeds the remaining balance on this invoice. "
                        + "Remaining balance: " + invoice.getTotalAmount().subtract(alreadyPaid));
            }

            Payment payment = Payment.builder()
                    .paymentDate(LocalDate.now())
                    .amount(request.getAmount())
                    .paymentMethod(method)
                    .reference(request.getReference())
                    .invoice(invoice)
                    .build();

            Payment saved = paymentRepository.save(payment);

            if (newTotalPaid.compareTo(invoice.getTotalAmount()) == 0) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {
                invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
            }
            invoiceRepository.save(invoice);

            log.info("Payment created with id: {} for invoice id: {}. Invoice status is now: {}",
                    saved.getId(), invoice.getId(), invoice.getStatus());

            try {
                emailService.sendInvoiceEbill(invoice.getId());
            } catch (Exception mailEx) {
                log.error("Payment saved but e-bill email failed: {}", mailEx.getMessage());
            }

            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createPayment() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Execute getAllPayments()");
        try {
            return paymentRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllPayments() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Execute getPaymentById()");
        try {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
            return toDto(payment);

        } catch (Exception e) {
            log.error("Error in getPaymentById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByInvoice(Long invoiceId) {
        log.info("Execute getPaymentsByInvoice()");
        try {
            return paymentRepository.findByInvoiceId(invoiceId).stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getPaymentsByInvoice() : " + e.getMessage(), e);
            throw e;
        }
    }

    private PaymentResponseDTO toDto(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getPaymentMethod().name(),
                payment.getReference(),
                payment.getInvoice().getId(),
                payment.getInvoice().getInvoiceNumber()
        );
    }
}
