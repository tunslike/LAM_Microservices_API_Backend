package com.lamsuite.authservice.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployerLoanProfile {
    private String COMPANY_PROFILE_ID;
    private Integer NO_OF_STAFF;
    private Double LOAN_LIMIT_PERCENT;
    private Double LOAN_INTEREST_RATE;
    private Integer LOAN_TENOR;
    private String PAYMENT_STRUCTURE;
    private LocalDateTime DATE_CREATED;
}
