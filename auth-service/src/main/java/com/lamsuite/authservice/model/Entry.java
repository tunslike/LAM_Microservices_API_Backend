package com.lamsuite.authservice.model;

import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Entry {
    private String CUSTOMER_ENTRY_ID;
    private String USERNAME;
    private String FULL_NAME;
    private String PHONE_NUMBER;
    private String EMAIL_ADDRESS;
    private String EMPLOYER_PROFILE_ID;
    private LocalDateTime DATE_CREATED;
    private Integer IS_LOGGED;
    private Boolean IS_RECORD_FOUND;
    private Boolean IS_EMPLOYER_FOUND;
    private Boolean IS_NOK_FOUND;
    private Boolean IS_DOCUMENT_FOUND;
    private EmployerLoanProfile employerLoanProfile;
}
