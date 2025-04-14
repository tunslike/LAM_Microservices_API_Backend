package com.lamsuite.authservice.model;

import com.lamsuite.authservice.model.Loan.LoanHistory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanDetails {
    private String CUSTOMER_ID;
    private String LOAN_ID;
    private String LOAN_NUMBER;
    private String LOAN_PURPOSE;
    private Double LOAN_AMOUNT;
    private Double MONTHLY_REPAYMENT;
    private Double TOTAL_REPAYMENT;
    private Double LOAN_TOTAL_REPAYMENT;
    private Double INTEREST_RATE;
    private Integer LOAN_TENOR;
    private String EMPLOYER_NAME;
    private Integer LOAN_STATUS;
    private String CREDIT_SCORE;
    private LoanHistory LOAN_HISTORY;
    private LocalDateTime AUTHORISE_DISBURSE_DATE;

}
