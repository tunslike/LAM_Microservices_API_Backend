package com.lamsuite.authservice.model.Loan;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDetails {
    private String BANK_ACCOUNT_ID;
    private String BANK_NAME;
    private String ACCOUNT_NUMBER;
    private String ACCOUNT_NAME;
    private LocalDateTime DATE_CREATED;
    private Integer IS_ACTIVE;
}
