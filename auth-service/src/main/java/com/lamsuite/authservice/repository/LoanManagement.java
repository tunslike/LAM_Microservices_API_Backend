package com.lamsuite.authservice.repository;

import com.lamsuite.authservice.dto.request.CustomerAccountDetailsDto;
import com.lamsuite.authservice.dto.request.CustomerLoanRequestDto;
import com.lamsuite.authservice.model.EmployerProfile;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.Loan.CustomerLoanRequest;

import java.util.List;

public interface LoanManagement<T> {

    boolean AddCustomerAccountDetails(CustomerAccountDetailsDto accountDetails) throws Exception;

    List<AccountDetails> FetchCustomerAccountDetails(String CustomerID);

    boolean SubmitCustomerLoanRequest(CustomerLoanRequestDto loanRequest) throws Exception;
}
