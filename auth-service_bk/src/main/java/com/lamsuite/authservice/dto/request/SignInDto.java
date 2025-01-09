package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignInDto {

    @NotEmpty(message = "Username is required.")
    @Email(message = "The email is not a valid email.")
    private String username;

    @NotBlank(message = "PIN Number is required.")
    private String pinNumber;

}
