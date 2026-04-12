package com.learning.payslip.service;

import com.learning.payslip.dto.PayslipJob;
import com.learning.payslip.dto.PayslipRequest;
import com.learning.payslip.dto.PayslipResponse;
import com.learning.payslip.dto.PayslipStatusResponse;
import com.learning.payslip.exception.PayslipGenerationException;
import com.learning.payslip.exception.PayslipNotFoundException;
import com.learning.payslip.exception.PayslipNotReadyException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PayslipService {

    private static final Logger  logger = LoggerFactory.getLogger(PayslipService.class);

    private final Map<String, PayslipJob> jobs = new ConcurrentHashMap<>();
    private final PayslipAsyncProcessor payslipAsyncProcessor;
    private final FileStorageService  fileStorageService;

    public PayslipService(PayslipAsyncProcessor payslipAsyncProcessor, FileStorageService fileStorageService) {
        this.payslipAsyncProcessor = payslipAsyncProcessor;
        this.fileStorageService = fileStorageService;
    }

    @Timed(value = "payslip.generation.time", description = "Time taken to generate payslip")
    @Counted(value = "payslip.generation.count", description = "Number of payslip generated")
    public PayslipResponse generatePayslip(PayslipRequest  payslipRequest) {
        logger.info("Generating payslip for employee: {}", payslipRequest.getEmployeeId());

        String jobId = UUID.randomUUID().toString();
        PayslipJob payslipJob = PayslipJob.builder().jobId(jobId).status("PROCESSING").createdAt(LocalDateTime.now()).request(payslipRequest).build();
        jobs.put(jobId, payslipJob);

        payslipAsyncProcessor.processPayslipAsync(payslipJob);

        logger.info("Payslip generation completed for employee: {}", payslipRequest.getEmployeeId());

        return PayslipResponse.builder().jobId(payslipJob.getJobId()).status("PROCESSING").message("Payslip generation started").build();
    }

    public PayslipStatusResponse getStatus(String jobId) {
        PayslipJob job = jobs.get(jobId);
        if(job == null)
            throw new PayslipNotFoundException("Payslip not found for jobId: " + jobId);
        return PayslipStatusResponse.builder().jobId(job.getJobId()).status(job.getStatus()).createdAt(null).completedAt(null).build();
    }

    public byte[] getPayslipFile(String jobId) {
        PayslipJob job = jobs.get(jobId);
        if (job == null) {
            throw new PayslipNotFoundException("Payslip not found for jobId: " + jobId);
        }
        if (!"COMPLETED".equals(job.getStatus())) {
            throw new PayslipNotReadyException("Payslip not ready. Current status: " + job.getStatus());
        }
        try {
            return fileStorageService.loadFile(job.getFilename());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load payslip file", e);
        }
    }
}
