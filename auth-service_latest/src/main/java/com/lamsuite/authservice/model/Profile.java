package com.lamsuite.authservice.model;

import lombok.Data;

@Data
public class Profile {
    private String customer_id;
    private String username;
    private String full_name;
    private String account_type;
    private String phone_number;
    private String email_address;
    private String employer_name;
}
