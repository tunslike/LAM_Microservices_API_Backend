package com.lamsuite.authservice.model.Loan;

import lombok.Data;

@Data
public class LoanSummaryBreakdown {
    private String month;
    private String principal;
    private String interest;
    private String balance;
}
