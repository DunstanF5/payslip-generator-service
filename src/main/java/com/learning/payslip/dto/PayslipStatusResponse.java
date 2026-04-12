package com.learning.payslip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipStatusResponse {

    private String jobId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

}
