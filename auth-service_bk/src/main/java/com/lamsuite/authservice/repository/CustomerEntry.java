package com.lamsuite.authservice.repository;

import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.*;
import com.lamsuite.authservice.model.VerifyOTPValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerEntry<T> {

    //create customer account
    boolean CreateCustomerAccount(CustomerDto customer);

    // validate customer record
    boolean ValidateCustomerRecord(ValidateCustomer customer);

    boolean PostCustomerTransaction(TransactionDto transaction);

    Profile FetchCustomerProfile(String CustomerID);

    //login customer account
    Entry AuthenticateCustomerAccount(SignInDto account);

    List<Transaction> FetchCustomerTransactions(String CustomerID);

    // reset customer PIN
    int ResetCustomerPIN(SignInDto account);

    // change customer PIN
    boolean ChangePINNumber(SignInDto account);

    // create registration OTP
    VerifyOTPValue CreateRegistrationOTP(ValidateCustomer details);

    // verify OTP value
    boolean VerifyRegistrationOTP(VerifyOTPResponse otpResponse);

    // update personal Data
    String UpdatePersonalData(PersonalDataUpdateDto record);

    boolean UpdateEmployerData(EmployerDataUpdateDto employerRecord);

    boolean UpdateNOKData(NOKDataUpdateDto nokRecord);



    boolean UploadCustomerDocuments(MultipartFile file, String doctype, String CustomerID) throws Exception;

    List<EmployerProfile> FetchEmployerProfiles();

    LoanDetails fetchCustomerLoanDetails(String CustomerID) throws Exception;

}
