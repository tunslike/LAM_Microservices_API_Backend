package com.lamsuite.authservice.controller;

import com.lamsuite.authservice.dto.Response;
import com.lamsuite.authservice.dto.request.CustomerAccountDetailsDto;
import com.lamsuite.authservice.dto.request.CustomerLoanRequestDto;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.services.LoanManagementService;
import jakarta.validation.Valid;
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

    @PostMapping("/createAccountDetails")
    public ResponseEntity createNewCustomerAccountDetails(@Valid @RequestBody CustomerAccountDetailsDto accountDetails) throws Exception {

        if(loanService.AddCustomerAccountDetails(accountDetails)) {
            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer account details created successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }// end of create New customer

    @PostMapping("/submitCustomerLoanRequest")
    public ResponseEntity createNewCustomerLoanRequest(@Valid @RequestBody CustomerLoanRequestDto loanRequest) throws Exception {

        if(loanService.SubmitCustomerLoanRequest(loanRequest)) {
            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer account details created successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }// end of create New customer

    // service to fetch customer account details
    @PostMapping("/fetchAccountDetails")
    public List<AccountDetails> getCustomerAccountDetails(@RequestBody String CustomerID) {

        List<AccountDetails> accountDetails = loanService.FetchCustomerAccountDetails(CustomerID);

        return accountDetails;
    }
    // end of service
}
