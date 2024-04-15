package com.lamsuite.authservice.model.Loan;

import lombok.Data;

@Data
public class LoanHistory {
    private String loan_purpose;
    private String date_disbursed;
    private Double loan_amount;
    private String loan_status;
}
