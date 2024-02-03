package com.lamsuite.authservice.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;


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
    private LocalDateTime DATE_CREATED;
    private Integer IS_LOGGED;
}
