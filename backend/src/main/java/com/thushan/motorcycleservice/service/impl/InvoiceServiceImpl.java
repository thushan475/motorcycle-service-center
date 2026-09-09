package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.InvoiceRequestDTO;
import com.thushan.motorcycleservice.dto.response.InvoiceDetailResponseDTO;
import com.thushan.motorcycleservice.dto.response.InvoiceResponseDTO;
import com.thushan.motorcycleservice.dto.response.PaymentResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderItemResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderSparePartResponseDTO;
import com.thushan.motorcycleservice.entity.Customer;
import com.thushan.motorcycleservice.entity.Invoice;
import com.thushan.motorcycleservice.entity.InvoiceStatus;
import com.thushan.motorcycleservice.entity.Payment;
import com.thushan.motorcycleservice.entity.ServiceOrder;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.CustomerRepository;
import com.thushan.motorcycleservice.repository.InvoiceRepository;
import com.thushan.motorcycleservice.repository.PaymentRepository;
import com.thushan.motorcycleservice.repository.ServiceOrderRepository;
import com.thushan.motorcycleservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request) {
        log.info("Execute createInvoice()");
        try {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
            ServiceOrder serviceOrder = serviceOrderRepository.findById(request.getServiceOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service order not found with id: " + request.getServiceOrderId()));

            if (invoiceRepository.existsByServiceOrderId(serviceOrder.getId())) {
                throw new DuplicateResourceException("An invoice already exists for service order id: " + serviceOrder.getId());
            }

            BigDecimal subtotal = serviceOrder.getTotalAmount();
            BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;

            if (discount.compareTo(subtotal) > 0) {
                throw new BadRequestException("Discount cannot be greater than the subtotal amount");
            }

            BigDecimal totalAmount = subtotal.subtract(discount);

            Invoice invoice = Invoice.builder()
                    .invoiceNumber(generateInvoiceNumber())
                    .invoiceDate(LocalDate.now())
                    .subtotal(subtotal)
                    .discount(discount)
                    .totalAmount(totalAmount)
                    .status(InvoiceStatus.UNPAID)
                    .customer(customer)
                    .serviceOrder(serviceOrder)
                    .build();

            Invoice saved = invoiceRepository.save(invoice);
            log.info("Invoice created with id: {}, invoice number: {}, total amount: {}",
                    saved.getId(), saved.getInvoiceNumber(), saved.getTotalAmount());
            return toDto(saved);

        } catch (Exception e) {
            log.error("Error in createInvoice() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {
        log.info("Execute getAllInvoices()");
        try {
            return invoiceRepository.findAll().stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getAllInvoices() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InvoiceResponseDTO getInvoiceById(Long id) {
        log.info("Execute getInvoiceById()");
        try {
            return toDto(findInvoiceOrThrow(id));

        } catch (Exception e) {
            log.error("Error in getInvoiceById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<InvoiceResponseDTO> getInvoicesByCustomer(Long customerId) {
        log.info("Execute getInvoicesByCustomer()");
        try {
            return invoiceRepository.findByCustomerId(customerId).stream().map(this::toDto).toList();

        } catch (Exception e) {
            log.error("Error in getInvoicesByCustomer() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteInvoice(Long id) {
        log.info("Execute deleteInvoice()");
        try {
            Invoice invoice = findInvoiceOrThrow(id);
            invoiceRepository.delete(invoice);
            log.info("Invoice deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteInvoice() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean invoiceExistsForServiceOrder(Long serviceOrderId) {
        log.info("Execute invoiceExistsForServiceOrder()");
        try {
            return invoiceRepository.existsByServiceOrderId(serviceOrderId);

        } catch (Exception e) {
            log.error("Error in invoiceExistsForServiceOrder() : " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<InvoiceResponseDTO> getUnpaidInvoices() {
        log.info("Execute getUnpaidInvoices()");
        try {
            List<InvoiceStatus> statuses = List.of(InvoiceStatus.UNPAID, InvoiceStatus.PARTIALLY_PAID);
            return invoiceRepository.findByStatusIn(statuses).stream().map(this::toDto).toList();
        } catch (Exception e) {
            log.error("Error in getUnpaidInvoices() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public int deleteCancelledInvoices() {
        log.info("Execute deleteCancelledInvoices()");
        try {
            return invoiceRepository.deleteByStatus(InvoiceStatus.CANCELLED);
        } catch (Exception e) {
            log.error("Error in deleteCancelledInvoices() : " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponseDTO getInvoiceDetail(Long id) {
        log.info("Execute getInvoiceDetail()");
        try {
            Invoice invoice = findInvoiceOrThrow(id);
            ServiceOrder so = invoice.getServiceOrder();

            List<ServiceOrderItemResponseDTO> services = so.getItems().stream()
                    .map(item -> new ServiceOrderItemResponseDTO(
                            item.getId(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal(),
                            item.getService().getId(),
                            item.getService().getName()
                    )).collect(Collectors.toList());

            List<ServiceOrderSparePartResponseDTO> spareParts = so.getSpareParts().stream()
                    .map(sp -> new ServiceOrderSparePartResponseDTO(
                            sp.getId(),
                            sp.getQuantity(),
                            sp.getUnitPrice(),
                            sp.getSubtotal(),
                            sp.getSparePart().getId(),
                            sp.getSparePart().getName()
                    )).collect(Collectors.toList());

            List<Payment> paymentList = paymentRepository.findByInvoiceId(invoice.getId());
            List<PaymentResponseDTO> payments = paymentList.stream()
                    .map(p -> new PaymentResponseDTO(
                            p.getId(),
                            p.getPaymentDate(),
                            p.getAmount(),
                            p.getPaymentMethod().name(),
                            p.getReference(),
                            p.getInvoice().getId(),
                            p.getInvoice().getInvoiceNumber()
                    )).collect(Collectors.toList());

            BigDecimal totalPaid = paymentList.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal balance = invoice.getTotalAmount().subtract(totalPaid);

            String brand = so.getMotorcycle().getMotorcycleBrand() != null
                    ? so.getMotorcycle().getMotorcycleBrand().getName() : "";
            String yearColor = so.getMotorcycle().getYear()
                    + (so.getMotorcycle().getColor() != null ? " / " + so.getMotorcycle().getColor() : "");

            return InvoiceDetailResponseDTO.builder()
                    .id(invoice.getId())
                    .invoiceNumber(invoice.getInvoiceNumber())
                    .invoiceDate(invoice.getInvoiceDate())
                    .subtotal(invoice.getSubtotal())
                    .discount(invoice.getDiscount())
                    .totalAmount(invoice.getTotalAmount())
                    .status(invoice.getStatus().name())
                    .customerId(invoice.getCustomer().getId())
                    .customerName(invoice.getCustomer().getName())
                    .customerEmail(invoice.getCustomer().getEmail())
                    .customerPhone(invoice.getCustomer().getPhone())
                    .customerAddress(invoice.getCustomer().getAddress())
                    .serviceOrderId(so.getId())
                    .orderDate(so.getOrderDate())
                    .motorcycleRegistration(so.getMotorcycle().getRegistrationNumber())
                    .motorcycleBrand(brand)
                    .motorcycleModelYear(yearColor)
                    .motorcycleColor(so.getMotorcycle().getColor())
                    .services(services)
                    .spareParts(spareParts)
                    .payments(payments)
                    .totalPaid(totalPaid)
                    .balance(balance)
                    .build();
        } catch (Exception e) {
            log.error("Error in getInvoiceDetail() : " + e.getMessage(), e);
            throw e;
        }
    }

    private Invoice findInvoiceOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private String generateInvoiceNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String invoiceNumber;
        do {
            int random = (int) (Math.random() * 9000) + 1000;
            invoiceNumber = "INV-" + datePart + "-" + random;
        } while (invoiceRepository.existsByInvoiceNumber(invoiceNumber));
        return invoiceNumber;
    }

    private InvoiceResponseDTO toDto(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getSubtotal(),
                invoice.getDiscount(),
                invoice.getTotalAmount(),
                invoice.getStatus().name(),
                invoice.getCustomer().getId(),
                invoice.getCustomer().getName(),
                invoice.getServiceOrder().getId()
        );
    }
}
