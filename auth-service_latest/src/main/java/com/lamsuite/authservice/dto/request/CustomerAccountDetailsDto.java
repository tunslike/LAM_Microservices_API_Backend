package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerAccountDetailsDto {
    @NotBlank(message = "Customer ID is required.")
    private String customerID;

    @NotBlank(message = "Bank name is required.")
    private String bankName;

    @NotBlank(message = "Bank ID is required.")
    private String bankID;

    @NotBlank(message = "Account number is required.")
    private String accountNumber;

    @NotBlank(message = "Account name is required.")
    private String accountName;

}
