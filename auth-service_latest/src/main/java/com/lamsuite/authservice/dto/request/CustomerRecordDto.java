package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class CustomerRecordDto {

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

    // EMPLOYER DETAILS
    @NotBlank(message = "The employer is required.")
    private String employer_name;

    @NotBlank(message = "The office address is required.")
    private String office_address;

    @NotBlank(message = "The employer area/location is required.")
    private String employer_area_locality;

    @NotBlank(message = "The employer state is required.")
    private String employer_state;

    @NotBlank(message = "The grade level is required.")
    private String grade_level;

    @NotBlank(message = "The sector is required.")
    private String sector;

    // NEXT OF KIN DETAILS
    @NotBlank(message = "The NOK Lastname is required.")
    private String nok_lastname;

    @NotBlank(message = "The NOK firstname is required.")
    private String nok_firstname;

    @NotBlank(message = "The NOK Relation is required.")
    private String nok_relationship;

    @NotBlank(message = "The NOK Gender is required.")
    private String nok_gender;

    @NotBlank(message = "The NOK Phone is required.")
    private String nok_phone;

    @NotBlank(message = "The NOK Email is required.")
    private String nok_email;

    @NotBlank(message = "The NOK Address is required.")
    private String nok_address;

    @NotBlank(message = "The NOK Area/location is required.")
    private String nok_area_locality;

    @NotBlank(message = "The NOK State is required.")
    private String nok_state;
}
