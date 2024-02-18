package com.lamsuite.authservice.dto;

import com.lamsuite.authservice.model.CustomerEmployerDetails;
import com.lamsuite.authservice.model.EmployerLoanProfile;
import com.lamsuite.authservice.model.Entry;
import com.lamsuite.authservice.model.LoanDetails;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private Response response;
    private Entry customer;
    private LoanDetails activeLoan;
    private EmployerLoanProfile employerloanProfile;
    private CustomerEmployerDetails customerEmployerDetails;
}
