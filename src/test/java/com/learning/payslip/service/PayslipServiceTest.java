package com.learning.payslip.service;

import com.learning.payslip.dto.PayslipJob;
import com.learning.payslip.dto.PayslipRequest;
import com.learning.payslip.dto.PayslipResponse;
import com.learning.payslip.exception.PayslipNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayslipServiceTest {
    @Mock
    private PayslipAsyncProcessor payslipAsyncProcessor;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PayslipService payslipService;

    private PayslipRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = PayslipRequest.builder()
                .employeeId("EMP001")
                .employeeName("John Smith")
                .companyName("Test Company")
                .companyAddress("123 Test Street")
                .periodStart(LocalDate.of(2026, 1, 1))
                .periodEnd(LocalDate.of(2026, 1, 31))
                .grossSalary(new BigDecimal("5000.00"))
                .taxDeduction(new BigDecimal("1000.00"))
                .netSalary(new BigDecimal("4000.00"))
                .build();
    }

    @Test
    void generatePayslip_ShouldReturnProcessingStatus() {
        // Act
        PayslipResponse response = payslipService.generatePayslip(validRequest);

        // Assert
        assertNotNull(response.getJobId());
        assertEquals("PROCESSING", response.getStatus());
        verify(payslipAsyncProcessor, times(1)).processPayslipAsync(any(PayslipJob.class));
    }

    @Test
    void getStatus_WhenJobNotFound_ShouldThrowException() {
        // Act & Assert
        assertThrows(PayslipNotFoundException.class, () -> {
            payslipService.getStatus("non-existent-id");
        });
    }

    @Test
    void getPayslipFile_WhenJobNotFound_ShouldThrowException() {
        // Act & Assert
        assertThrows(PayslipNotFoundException.class, () -> {
            payslipService.getPayslipFile("non-existent-id");
        });
    }
}
