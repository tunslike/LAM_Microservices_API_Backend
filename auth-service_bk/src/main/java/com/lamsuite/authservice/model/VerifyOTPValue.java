package com.lamsuite.authservice.model;

import lombok.Data;

@Data
public class VerifyOTPValue {
    private String otp_value;
    private String verificationId;
}
