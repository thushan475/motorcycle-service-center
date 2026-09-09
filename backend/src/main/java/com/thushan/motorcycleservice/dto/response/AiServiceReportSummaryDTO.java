package com.thushan.motorcycleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiServiceReportSummaryDTO {
    private Long serviceOrderId;
    private String summaryTitle;
    private String generatedSummary;
    private String recommendation;
    private String riskLevel;
}
