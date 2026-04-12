package com.learning.payslip.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PayslipJob {
    private String jobId;
    private String status;  // PENDING, PROCESSING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private PayslipRequest request;
    private byte[] pdfData;
    private String filename;
}
