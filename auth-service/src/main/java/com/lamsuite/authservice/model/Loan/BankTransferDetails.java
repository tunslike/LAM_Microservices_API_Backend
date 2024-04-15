package com.lamsuite.authservice.model.Loan;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankTransferDetails {
    private String bank_id;
    private String bank_name;
    private String account_name;
    private String account_number;
}
