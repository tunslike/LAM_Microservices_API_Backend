package com.lamsuite.authservice.dto;

import com.lamsuite.authservice.model.Loan.CustomerLoanSummary;
import com.lamsuite.authservice.model.Loan.LoanRepayment;
import com.lamsuite.authservice.model.LoanDetails;
import lombok.Data;

import java.util.List;

@Data
public class LoanDetailsResponse {
    private Response response;
    private LoanDetails loanDetails;
    private CustomerLoanSummary breakdown;
    private LoanRepayment repayment;
}
