package com.learning.payslip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipRequest {

    @NotBlank
    private String employeeId;
    @NotBlank
    private String employeeName;
    @NotNull
    private LocalDate periodStart;
    @NotNull
    private LocalDate periodEnd;
    @NotNull @Positive
    private BigDecimal grossSalary;
    @NotNull @PositiveOrZero
    private BigDecimal taxDeduction;
    @NotNull @Positive
    private BigDecimal netSalary;
    @NotBlank
    private String companyName;
    @NotBlank
    private String companyAddress;

}
