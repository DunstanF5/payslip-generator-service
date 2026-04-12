package com.learning.payslip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.payslip.dto.PayslipRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PayslipControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generatePayslip_WithValidRequest_ShouldReturn202() throws Exception {
        PayslipRequest request = PayslipRequest.builder()
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

        mockMvc.perform(post("/api/payslips")
                        .header("Api-Key", "my-secret-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void generatePayslip_WithMissingFields_ShouldReturn400() throws Exception {
        PayslipRequest request = PayslipRequest.builder()
                .employeeId("")  // Invalid - blank
                .build();

        mockMvc.perform(post("/api/payslips")
                        .header("Api-Key", "my-secret-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generatePayslip_WithoutApiKey_ShouldReturn401() throws Exception {
        PayslipRequest request = PayslipRequest.builder()
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

        mockMvc.perform(post("/api/payslips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStatus_WithNonExistentJob_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/payslips/non-existent-id/status")
                        .header("Api-Key", "my-secret-api-key"))
                .andExpect(status().isBadRequest());
    }
}
