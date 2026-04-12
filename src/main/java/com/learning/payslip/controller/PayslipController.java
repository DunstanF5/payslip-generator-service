package com.learning.payslip.controller;

import com.learning.payslip.dto.PayslipRequest;
import com.learning.payslip.dto.PayslipResponse;
import com.learning.payslip.dto.PayslipStatusResponse;
import com.learning.payslip.service.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    @PostMapping
    @Operation(summary = "Generate a payslip", description = "Submits a payslip generation request")
    @ApiResponse(responseCode = "202", description = "Payslip generation accepted")
    public ResponseEntity<PayslipResponse> generatePayslip(@Valid @RequestBody PayslipRequest payslipRequest) {
        PayslipResponse payslipResponse = payslipService.generatePayslip(payslipRequest);
        return ResponseEntity.accepted().body(payslipResponse);
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<PayslipStatusResponse> getStatus(@PathVariable("jobId") String jobId) {
        PayslipStatusResponse payslipStatusResponse = payslipService.getStatus(jobId);
        return ResponseEntity.ok(payslipStatusResponse);
    }

    @GetMapping("/{jobId}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable("jobId") String jobId) {
        byte[] pdfData = payslipService.getPayslipFile(jobId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=payslip-" + jobId + ".pdf")
                .body(pdfData);
    }
}
