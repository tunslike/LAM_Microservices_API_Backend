package com.lamsuite.authservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EntryResponse {
    private Response Response;
    private UUID CUSTOMER_ENTRY_ID;
    private String FULL_NAME;
    private String EMAIL_ADDRESS;
}
