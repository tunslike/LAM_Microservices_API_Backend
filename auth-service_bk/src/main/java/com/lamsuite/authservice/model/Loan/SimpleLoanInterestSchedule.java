package com.lamsuite.authservice.model.Loan;

import lombok.Data;

@Data
public class SimpleLoanInterestSchedule {
    public String PaymentMonth;
    public String LoanBalance;
    public String MonthlyRepayment;
    public String InterestPaid;
    public String PrincipalPaid;
    public String NewBalance;
}
