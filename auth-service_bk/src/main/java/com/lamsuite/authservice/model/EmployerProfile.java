package com.lamsuite.authservice.model;


import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployerProfile {
    private String PROFILE_ID;
    private String COMPANY_NAME;
    private String ADDRESS;
    private String AREA_LOCALITY;
    private String STATE;
    private String CONTACT_PERSON;
    private String CONTACT_EMAIL;
    private LocalDateTime DATE_CREATED;
}
