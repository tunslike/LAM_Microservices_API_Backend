package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
public class CustomerDto {

    @NotBlank(message = "The full name is required.")
    private String full_name;

    @NotBlank(message = "The phone number is required.")
    private String phoneNumber;

    @NotEmpty(message = "The email is required.")
    @Email(message = "The email is not a valid email.")
    private String emailAddress;

    @NotEmpty(message = "The account type is required.")
    private String account_type;

    @NotEmpty(message = "The pin Number is required.")
    private String pinNumber;

    @NotEmpty(message = "The employer profile ID is required.")
    private String employer_profile_id;
}
