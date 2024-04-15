package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CalculateLoanSummaryDto {
    @NotBlank(message = "Customer ID is required.")
    private String customerID;

    @NotBlank(message = "Loan amount is required.")
    private String loanAmount;

    @NotBlank(message = "Loan tenor is required.")
    private String loanTenor;

    @NotBlank(message = "Loan interest is required.")
    private String loanInterest;

}
