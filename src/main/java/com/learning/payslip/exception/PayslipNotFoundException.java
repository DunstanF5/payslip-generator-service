package com.learning.payslip.exception;

public class PayslipNotFoundException extends RuntimeException{

    public PayslipNotFoundException(String message) {
        super(message);
    }

}
