package com.lamsuite.authservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private String customer_id;
    private String transaction_type;
    private Double amount;
    private String summary;
    private String narration;
    private String request_status;
    private String payment_status;
    private String payment_reference;
    private String payment_response_date;
}
