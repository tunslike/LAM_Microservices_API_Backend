package com.lamsuite.authservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Transaction {
    private String transaction_id;
    private String customer_id;
    private Double amount;
    private String transaction_type;
    private String summary;
    private String narration;
    private Integer request_status;
    private Integer payment_status;
    private String payment_reference;
    private LocalDateTime payment_response_date;
    private LocalDateTime date_created;
    private String created_by;
}
