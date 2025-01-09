package com.lamsuite.authservice.model.Loan;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanHistory {
    private String loan_number;
    private String loan_id;
    private String loan_tenor;
    private String loan_purpose;
    private String date_disbursed;
    private Double loan_amount;
    private String loan_status;
    private LocalDateTime loan_date;
}
