package com.thushan.motorcycleservice.controller;

import com.thushan.motorcycleservice.constant.CommonResponse;
import com.thushan.motorcycleservice.dto.response.AiServiceReportSummaryDTO;
import com.thushan.motorcycleservice.service.AiReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiReportController {

    private final AiReportService aiReportService;

    @GetMapping("/service-report/{serviceOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommonResponse<AiServiceReportSummaryDTO> generateServiceReport(
            @PathVariable Long serviceOrderId) {
        AiServiceReportSummaryDTO summary = aiReportService.generateServiceReportSummary(serviceOrderId);
        return new CommonResponse<>(200, "AI service report summary generated successfully", summary);
    }
}
