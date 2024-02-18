package com.lamsuite.authservice.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanDetails {
    private String LOAN_ID;
    private Double CURRENT_LOAN_BALANCE;
    private Double NEXT_REPAYMENT_AMOUNT;
    private String LOAN_ACCOUNT_NUMBER;
    private LocalDateTime DATE_CREATED;
    private LocalDateTime DATE_APPROVED;
    private Integer LOAN_STATUS;
}
