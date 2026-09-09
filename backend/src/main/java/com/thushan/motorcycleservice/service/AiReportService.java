package com.thushan.motorcycleservice.service;

import com.thushan.motorcycleservice.dto.response.AiServiceReportSummaryDTO;

public interface AiReportService {
    AiServiceReportSummaryDTO generateServiceReportSummary(Long serviceOrderId);
}
