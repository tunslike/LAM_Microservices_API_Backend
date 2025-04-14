package com.lamsuite.authservice.dto.request;

import lombok.Data;

@Data
public class VerifyOTP {
    private String full_name;
    private String emailAddress;
    private String mobilePhone;
}
