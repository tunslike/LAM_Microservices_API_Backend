package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ValidateCustomer {

    @NotBlank(message = "The full name is required.")
    private String full_name;

    @NotBlank(message = "The phone number is required.")
    private String phoneNumber;

    @NotEmpty(message = "The email is required.")
    @Email(message = "The email is not a valid email.")
    private String emailAddress;
}
