package com.lamsuite.authservice.dto.request;

import lombok.Data;

@Data
public class VerifyOTPResponse {
    private String verification_id;
    private String otp_value;
    private String phoneNumber;
}
