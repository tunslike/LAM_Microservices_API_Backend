package com.lamsuite.authservice.model.Loan;

import lombok.Data;
import java.util.List;

@Data
public class SimpleInterestLoanSummary {
    private String LoanAmount;
    private String LoanTenor;
    private String LoanRate;
    private String MonthlyPrincipal;
    private String MonthlyInterest;
    private String MonthlyRepayment;
    private String TotalInterestPaid;
    private String TotalLoanPayment;
    private String PreApprovedAmount;
    private List<SimpleLoanInterestSchedule> RepaymentSchedule;
}
