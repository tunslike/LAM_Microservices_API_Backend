package com.lamsuite.authservice.model.Loan;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerLoanSummary {
    private String customer_id;
    private String monthly_repayment;
    private String total_repayment;
    private List<LoanSummaryBreakdown> repayment_schedule;
}
