package com.lamsuite.authservice.dto;

import com.lamsuite.authservice.model.Entry;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private Response response;
    private Entry customer;
}
