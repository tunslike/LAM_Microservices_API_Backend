package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerLoanRequestDto {
    @NotBlank(message = "Customer ID is required.")
    private String customerID;

    @NotBlank(message = "Loan amount is required.")
    private String loanAmount;

    @NotBlank(message = "Loan tenor is required.")
    private String loanTenor;

    @NotBlank(message = "Loan purpose is required.")
    private String loanPurpose;

    @NotBlank(message = "Account ID is required.")
    private String accountID;
}
