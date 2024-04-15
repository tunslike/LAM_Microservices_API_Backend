package com.lamsuite.authservice.repository;

import com.lamsuite.authservice.dto.LoanDetailsResponse;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.Loan.BankTransferDetails;
import com.lamsuite.authservice.model.Loan.CustomerLoanSummary;
import com.lamsuite.authservice.model.LoanDetails;

import java.util.List;

public interface LoanManagement<T> {

    AccountDetails AddCustomerAccountDetails(CustomerAccountDetailsDto accountDetails) throws Exception;

    AccountDetails FetchCustomerAccountDetails(GenerateTokenDto CustomerID);

    boolean SubmitCustomerLoanRequest(CustomerLoanRequestDto loanRequest) throws Exception;

    CustomerLoanSummary CalculateAmortizeSchedule(CalculateLoanSummaryDto loanDetails) throws Exception;

    String ValidateCustomerVerification(String CustomerID) throws Exception;

    String VerifyCustomerVerification(String CipherText) throws Exception;

    LoanDetails fetchCustomerLoanDetails(FetchLoanDetailsDto loan) throws Exception;

    BankTransferDetails fetchBankTransferDetails() throws Exception;

    LoanDetails ValidateCustomerLoanDetails(CustomerIDdto customer) throws Exception;
}
