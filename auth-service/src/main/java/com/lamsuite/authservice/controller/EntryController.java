package com.lamsuite.authservice.controller;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.Response;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.*;
import com.lamsuite.authservice.services.CustomerEntryService;
import com.lamsuite.authservice.services.LoanManagementService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
public class EntryController {

    //customer service
    private final CustomerEntryService account;

    //request to create new customer
    @PostMapping("/newCustomer")
    public ResponseEntity createNewCustomerAccount(@Valid @RequestBody CustomerDto customer) {

        if(account.CreateCustomerAccount(customer)) {
            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer created successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }// end of create New customer

    // service to return employer profiles
    @GetMapping("/getEmployerProfiles")
    public List<EmployerProfile> fetchEmployerProfile() {
        return account.FetchEmployerProfiles();
    }
    // end of service


    @PostMapping("/updatePersonalData")
    public ResponseEntity updatePersonalData(@Valid @RequestBody PersonalDataUpdateDto personalRecord) {

        Entry customerEntry = new Entry();
        EntryResponse responseEntry = new EntryResponse();

        String customerID = account.UpdatePersonalData(personalRecord);

        if(!Objects.equals(customerID, "")) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer record created successfully");

            responseEntry.setResponse(response);
            responseEntry.setCUSTOMER_ENTRY_ID(UUID.fromString(customerID));

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);

    }
    // end of update customer record

    @PostMapping("/updateEmployerData")
    public ResponseEntity updateEmployerData(@Valid @RequestBody EmployerDataUpdateDto employerRecord) {

        Entry customerEntry = new Entry();
        EntryResponse responseEntry = new EntryResponse();

        boolean createStatus = account.UpdateEmployerData(employerRecord);

        if(createStatus) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Employer record created successfully");

            responseEntry.setResponse(response);

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);

    }
    // end of update employer record

    @PostMapping("/updateNOKData")
    public ResponseEntity<EntryResponse> updateNOKData(@Valid @RequestBody NOKDataUpdateDto nokRecord) {

        Entry customerEntry = new Entry();
        EntryResponse responseEntry = new EntryResponse();

        boolean createStatus = account.UpdateNOKData(nokRecord);

        if(createStatus) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("NOK record created successfully");

            responseEntry.setResponse(response);

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);

    }
    // end of update nok record

    // service to upload documents
    @PostMapping("/uploadDocuments")
    public ResponseEntity<String> uploadCustomerDocuments(@RequestParam("file") MultipartFile file) throws Exception {

        if(account.UploadCustomerDocuments(file)) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer document uploaded successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }
    // end of function

    //service to reset pin
    @PostMapping("/resetPin")
    public ResponseEntity resetCustomerPIN(@Valid @RequestBody SignInDto login) {

        if(account.ResetCustomerPIN(login)) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer pin has been reset successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    //end of service

    // service to login
    @PostMapping("/login")
    public ResponseEntity authenticateCustomer(@Valid @RequestBody SignInDto login) throws Exception {

        Entry customerEntry = new Entry();
        EmployerLoanProfile emprofile = null;
        CustomerEmployerDetails customerEmployerDetails = null;
        LoginResponse responseEntry = new LoginResponse();
        LoanDetails loanDetails = new LoanDetails();

        customerEntry = account.AuthenticateCustomerAccount(login);

        if(customerEntry != null) {

            //get company loan profile
            emprofile = account.FetchEmployerLoanProfile(customerEntry.getCUSTOMER_ENTRY_ID());
            customerEmployerDetails = account.FetchCustomerEmployerLoanProfile(customerEntry.getCUSTOMER_ENTRY_ID());
            loanDetails = account.fetchCustomerLoanDetails(customerEntry.getCUSTOMER_ENTRY_ID());

            Response response = new Response();

            response.setResponseCode(200);
            response.setResponseMessage("Customer Found!");

            responseEntry.setResponse(response);
            responseEntry.setCustomer(customerEntry);
            responseEntry.setActiveLoan(loanDetails);
            responseEntry.setCustomerEmployerDetails(customerEmployerDetails);
            responseEntry.setEmployerloanProfile(emprofile);

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);
    }

}
