package com.lamsuite.authservice.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanApplication {
    private String CUSTOMER_ENTRY_ID;
    private String USERNAME;
    private String FULL_NAME;
    private String PHONE_NUMBER;
    private String EMAIL_ADDRESS;
    private LocalDateTime DATE_CREATED;
    private Integer IS_LOGGED;
}
