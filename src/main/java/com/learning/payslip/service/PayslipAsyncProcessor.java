package com.learning.payslip.service;

import com.learning.payslip.dto.PayslipJob;
import com.learning.payslip.dto.PayslipRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayslipAsyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PayslipAsyncProcessor.class);

    private final PdfGenerationService pdfGenerationService;
    private final FileStorageService fileStorageService;

    public PayslipAsyncProcessor(PdfGenerationService pdfGenerationService, FileStorageService fileStorageService) {
        this.pdfGenerationService = pdfGenerationService;
        this.fileStorageService = fileStorageService;
    }

    @Async
    public void processPayslipAsync(PayslipJob job){

        try {
            logger.info("Starting PDF generation for jobId: {}", job.getJobId());

            // Build template data from request
            PayslipRequest request = job.getRequest();
            Map<String, Object> data = new HashMap<>();
            data.put("companyName", request.getCompanyName());
            data.put("companyAddress", request.getCompanyAddress());
            data.put("employeeId", request.getEmployeeId());
            data.put("employeeName", request.getEmployeeName());
            data.put("periodStart", request.getPeriodStart().toString());
            data.put("periodEnd", request.getPeriodEnd().toString());
            data.put("grossSalary", request.getGrossSalary());
            data.put("taxDeduction", request.getTaxDeduction());
            data.put("netSalary", request.getNetSalary());
            data.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Generate PDF
            byte[] pdfBytes = pdfGenerationService.generatePdf("payslip-template", data);

            // In processPayslipAsync method, after generating PDF:
            String filename = "payslip-" + job.getJobId() + ".pdf";
            fileStorageService.saveFile(filename, pdfBytes);
            job.setFilename(filename);  // Store filename instead of raw bytes

            job.setStatus("COMPLETED");
            job.setCompletedAt(LocalDateTime.now());

            logger.info("PDF generated successfully for jobId: {}, size: {} bytes", job.getJobId(), pdfBytes.length);

        } catch (Exception e) {
            logger.error("Failed to generate PDF for jobId: {}", job.getJobId(), e);
            job.setStatus("FAILED");
            job.setCompletedAt(LocalDateTime.now());
        }

    }
}
