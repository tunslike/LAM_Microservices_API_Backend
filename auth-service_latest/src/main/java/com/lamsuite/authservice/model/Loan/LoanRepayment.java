package com.lamsuite.authservice.model.Loan;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanRepayment {
    private String repayment_id;
    private String narration;
    private Double repayment_amount;
    private LocalDateTime payment_date;
    private String payment_channel;
}
