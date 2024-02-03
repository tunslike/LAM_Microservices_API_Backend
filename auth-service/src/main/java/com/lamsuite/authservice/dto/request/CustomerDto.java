package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
public class CustomerDto {

    @NotBlank(message = "The full name is required.")
    private String fullname;

    @NotBlank(message = "The phone number is required.")
    private String phoneNumber;

    @NotEmpty(message = "The email is required.")
    @Email(message = "The email is not a valid email.")
    private String emailAddress;
}
