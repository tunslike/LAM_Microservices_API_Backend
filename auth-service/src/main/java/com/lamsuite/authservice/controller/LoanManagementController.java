package com.lamsuite.authservice.controller;

import com.lamsuite.authservice.dto.LoanDetailsResponse;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.Response;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.CustomerEmployerDetails;
import com.lamsuite.authservice.model.EmployerLoanProfile;
import com.lamsuite.authservice.model.Entry;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.Loan.BankTransferDetails;
import com.lamsuite.authservice.model.Loan.CustomerLoanSummary;
import com.lamsuite.authservice.model.Loan.LoanRepayment;
import com.lamsuite.authservice.model.LoanDetails;
import com.lamsuite.authservice.services.LoanManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/v1/loanService")
@AllArgsConstructor
public class LoanManagementController {

    private final LoanManagementService loanService;


    // service to fetch customer loan Details
    @PostMapping("/fetchCustomerLoanDetails")
    public ResponseEntity getCustomerLoanDetails(@Valid @RequestBody FetchLoanDetailsDto loan) throws Exception {

        LoanDetailsResponse loanResponse = new LoanDetailsResponse();
        LoanDetails loanDetails = new LoanDetails();
        CustomerLoanSummary loanSummary = new CustomerLoanSummary();
        LoanRepayment repayment = new LoanRepayment();

        loanDetails = loanService.fetchCustomerLoanDetails(loan);

        CalculateLoanSummaryDto loanValues = new CalculateLoanSummaryDto();

        if(loanDetails != null) {

            loanValues.setCustomerID(loanDetails.getCUSTOMER_ID());
            loanValues.setLoanAmount(loanDetails.getLOAN_AMOUNT().toString());
            loanValues.setLoanInterest(loanDetails.getINTEREST_RATE().toString());
            loanValues.setLoanTenor(loanDetails.getLOAN_TENOR().toString());

            loanSummary = loanService.CalculateAmortizeSchedule(loanValues);

            Response response = new Response();

            response.setResponseCode(200);
            response.setResponseMessage("Customer Found!");

            loanResponse.setResponse(response);
            loanResponse.setBreakdown(loanSummary);
            loanResponse.setLoanDetails(loanDetails);
            loanResponse.setRepayment(repayment);

            return new ResponseEntity(loanResponse, HttpStatus.OK);
        }

        return new ResponseEntity(loanDetails, HttpStatus.BAD_REQUEST);
    }
    // end of service

    @PostMapping("/createAccountDetails")
    public ResponseEntity createNewCustomerAccountDetails(@Valid @RequestBody CustomerAccountDetailsDto accountDetails) throws Exception {

        AccountDetails details = new AccountDetails();

        details = loanService.AddCustomerAccountDetails(accountDetails);

        return new ResponseEntity(details, HttpStatus.OK);

    }// end of create New customer

    @GetMapping("/validateCustomerVerification")
    public ResponseEntity validateCustomerToken(@RequestParam String loadID) throws  Exception {

        String response = loanService.ValidateCustomerVerification(loadID);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/verifyCustomerVerification")
    public ResponseEntity verifyCustomerToken(@RequestParam String CipherText) throws  Exception {

        String response = loanService.VerifyCustomerVerification(CipherText);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/calculateLoanRequestSummary")
    public ResponseEntity calculateClientLoanRequest(@Valid @RequestBody CalculateLoanSummaryDto loanDetails) throws  Exception {
        CustomerLoanSummary loanSummary = new CustomerLoanSummary();

        loanSummary = loanService.CalculateAmortizeSchedule(loanDetails);

        return new ResponseEntity(loanSummary, HttpStatus.OK);
    }
    @PostMapping("/submitCustomerLoanRequest")
    public ResponseEntity createNewCustomerLoanRequest(@Valid @RequestBody CustomerLoanRequestDto loanRequest) throws Exception {

        if(loanService.SubmitCustomerLoanRequest(loanRequest)) {
            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer loan request submitted successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }// end of create New customer

    //service to validate customer loan
    @PostMapping("/confirmCustomerLoan")
    public ResponseEntity confirmCustomerLoan(@Valid @RequestBody CustomerIDdto customer) throws Exception {

        LoanDetails accountDetails = loanService.ValidateCustomerLoanDetails(customer);

        return new ResponseEntity(accountDetails, HttpStatus.OK);

    }
    //end of service

    @GetMapping("/fetchBankTransferDetails")
    public ResponseEntity fetchBankDetails() throws Exception {

        BankTransferDetails bankTransferDetails = loanService.fetchBankTransferDetails();

        return new ResponseEntity(bankTransferDetails, HttpStatus.OK);

    }
    //end of service

    // service to fetch customer account details
    @PostMapping("/fetchAccountDetails")
    public AccountDetails getCustomerAccountDetails(@RequestBody GenerateTokenDto customer) {

        AccountDetails accountDetails = loanService.FetchCustomerAccountDetails(customer);

        return accountDetails;
    }
    // end of service
}
