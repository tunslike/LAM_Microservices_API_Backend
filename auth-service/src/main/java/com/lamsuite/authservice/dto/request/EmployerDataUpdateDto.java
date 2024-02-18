package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmployerDataUpdateDto {

    @NotBlank(message = "Customer ID is required.")
    private String customerID;

    @NotBlank(message = "Employer profile ID is required.")
    private String employerProfileID;

    @NotBlank(message = "Sector is required.")
    private String sector;

    @NotBlank(message = "Sector is required.")
    private String grade_level;

    @NotBlank(message = "Service length is required.")
    private String service_length;

    @NotBlank(message = "Staff ID Number is required.")
    private String staff_id_number;

    @NotBlank(message = "Salary payment is required.")
    private String salary_payment_date;

    @NotBlank(message = "Annual salary is required.")
    private String annual_salary;

}
