package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.response.AiServiceReportSummaryDTO;
import com.thushan.motorcycleservice.entity.ServiceOrder;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.ServiceOrderRepository;
import com.thushan.motorcycleservice.service.AiReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiReportServiceImpl implements AiReportService {

    private final ServiceOrderRepository serviceOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public AiServiceReportSummaryDTO generateServiceReportSummary(Long serviceOrderId) {
        ServiceOrder order = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found with id: " + serviceOrderId));

        String servicesList = order.getItems().stream()
                .map(i -> i.getService().getName() + " (x" + i.getQuantity() + ")")
                .collect(Collectors.joining(", "));

        String partsList = order.getSpareParts().stream()
                .map(p -> p.getSparePart().getName() + " (x" + p.getQuantity() + ")")
                .collect(Collectors.joining(", "));

        if (servicesList.isEmpty()) {
            servicesList = "No services recorded";
        }
        if (partsList.isEmpty()) {
            partsList = "No spare parts used";
        }

        String bike = order.getMotorcycle().getRegistrationNumber();
        String customer = order.getCustomer().getName();
        BigDecimal total = order.getTotalAmount();

        StringBuilder summary = new StringBuilder();
        summary.append("Service Report Summary for ").append(bike)
                .append(" owned by ").append(customer).append(". ");
        summary.append("Completed services: ").append(servicesList).append(". ");
        summary.append("Spare parts replaced/used: ").append(partsList).append(". ");
        summary.append("Total service value: LKR ").append(total).append(". ");

        if (order.getRemarks() != null && !order.getRemarks().isBlank()) {
            summary.append("Technician notes: ").append(order.getRemarks()).append(". ");
        }

        String recommendation;
        String riskLevel;
        long partCount = order.getSpareParts().size();

        if (partCount >= 4 || total.compareTo(new BigDecimal("50000")) > 0) {
            recommendation = "Major service completed. Recommend follow-up inspection within 30 days and regular oil/filter checks.";
            riskLevel = "MEDIUM";
        } else if (partCount >= 2) {
            recommendation = "Standard maintenance performed. Advise customer to monitor performance and return for next scheduled service.";
            riskLevel = "LOW";
        } else {
            recommendation = "Minor service completed. Vehicle appears in good condition. Continue regular servicing schedule.";
            riskLevel = "LOW";
        }

        return AiServiceReportSummaryDTO.builder()
                .serviceOrderId(serviceOrderId)
                .summaryTitle("AI Generated Service Report – " + bike)
                .generatedSummary(summary.toString())
                .recommendation(recommendation)
                .riskLevel(riskLevel)
                .build();
    }
}
