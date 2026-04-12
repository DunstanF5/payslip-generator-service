package com.learning.payslip.exception;

public class PayslipNotReadyException extends RuntimeException{
    public PayslipNotReadyException(String message) {
        super(message);
    }
}
