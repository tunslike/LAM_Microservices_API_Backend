package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PersonalDataUpdateDto {
    // PERSONAL DETAILS
    @NotBlank(message = "The last name is required.")
    private String lastname;

    @NotBlank(message = "The first name is required.")
    private String firstname;


    private String other_name;

    @NotBlank(message = "The gender is required.")
    private String gender;

    @NotBlank(message = "The day of birth is required.")
    private String date_of_birth;


    private String place_of_birth;

    @NotBlank(message = "The phone number is required.")
    private String phone_number;

    @NotEmpty(message = "The email is required.")
    @Email(message = "The email is not a valid email.")
    private String emailAddress;

    @NotBlank(message = "The state of origin is required.")
    private String state_of_origin;

    @NotBlank(message = "The nationality is required.")
    private String nationality;

    @NotBlank(message = "The house address is required.")
    private String address;

    @NotBlank(message = "The area/locality is required.")
    private String area_location;

    @NotBlank(message = "The state is required.")
    private String state;
}
