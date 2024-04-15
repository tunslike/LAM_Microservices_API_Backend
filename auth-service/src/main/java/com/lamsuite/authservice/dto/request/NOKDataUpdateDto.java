package com.lamsuite.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NOKDataUpdateDto {
    @NotBlank(message = "Customer ID is required.")
    private String customer_id;

    @NotBlank(message = "NOK lastname is required.")
    private String nok_lastname;

    @NotBlank(message = "NOK firstname is required.")
    private String nok_firstname;

    @NotBlank(message = "NOK gender is required.")
    private String nok_gender;

    @NotBlank(message = "NOK relationship is required.")
    private String nok_relationship;

    @NotBlank(message = "NOK phone required.")
    private String nok_phone;

    @NotBlank(message = "NOK email is required.")
    private String nok_email;

    @NotBlank(message = "NOK address is required.")
    private String nok_address;

    @NotBlank(message = "NOK area locality is required.")
    private String nok_areaLocality;

    @NotBlank(message = "NOK state is required.")
    private String nok_state;
}
