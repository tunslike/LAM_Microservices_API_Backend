package com.lamsuite.authservice.controller;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.Response;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.*;
import com.lamsuite.authservice.services.CustomerEntryService;
import com.lamsuite.authservice.services.LoanManagementService;
import com.lamsuite.authservice.utilities.Utilities;
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

    // request to validate customer duplicate
    @PostMapping("/validateEntryRecord")
    public ResponseEntity validateCustomerRecord(@Valid @RequestBody ValidateCustomer customer) {

        VerifyOTPValue otpValue = null;

        if(account.ValidateCustomerRecord(customer)) {

            otpValue = account.CreateRegistrationOTP(customer);

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("No duplicate record found!");

            return new ResponseEntity(otpValue, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Duplicate found!");

            return new ResponseEntity(response, HttpStatus.OK);
        }
    }
    // end of function

    // request to generate registration OTP
    @PostMapping("/generateRegistrationOTP")
    public ResponseEntity validateCustomerRecord(@Valid @RequestBody VerifyOTP details) {

        VerifyOTPValue response = null;

        //response = account.CreateRegistrationOTP(details);

        if(response != null) {

            return new ResponseEntity(response, HttpStatus.OK);
        }

        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
    // end of function

    // request to generate registration OTP
    @PostMapping("/verifyRegistrationOTP")
    public ResponseEntity verifyRegistrationOTP(@Valid @RequestBody VerifyOTPResponse details) {

        if(account.VerifyRegistrationOTP(details)) {

            return new ResponseEntity("verified", HttpStatus.OK);
        }

        return new ResponseEntity("unverified", HttpStatus.OK);
    }
    // end of function

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

    @GetMapping("/fetchTransaction")
    public  List<Transaction> fetchTransaction(@RequestParam String CustomerID) throws Exception {
       return account.FetchCustomerTransactions(CustomerID);
    }
    //end of service

    // service to post transaction request
    @PostMapping("/saveTransaction")
    public ResponseEntity postTransaction(@Valid @RequestBody TransactionDto request) {

        Transaction transaction = new Transaction();
        EntryResponse responseEntry = new EntryResponse();

        boolean postTransaction = account.PostCustomerTransaction(request);

        if(postTransaction) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Transaction record posted successfully");

            responseEntry.setResponse(response);

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);

    }
    // end of service

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

    //service to fetch profile
    @GetMapping("/fetchCustomerProfile")
    public ResponseEntity fetchViewCustomerProfile(@RequestParam String CustomerID) {

        Profile profile = new Profile();

        profile = account.FetchCustomerProfile(CustomerID);

        return new ResponseEntity(profile, HttpStatus.OK);
    }
    //end of service

    // service to upload documents
    @PostMapping("/uploadDocuments")
    public ResponseEntity<String> uploadCustomerDocuments(@RequestParam("file") MultipartFile file,
                                                          @RequestHeader("x-doctype") String headerData,
                                                          @RequestParam("customerID") String CustomerID) throws Exception {

        if(account.UploadCustomerDocuments(file, headerData, CustomerID)) {

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

    // service to send OTP
    @GetMapping("/sendOTPValidation")
    public ResponseEntity sendOTPValidation(@RequestParam String email) {
        return new ResponseEntity("response", HttpStatus.OK);
    }

    //service to reset pin
    @PostMapping("/resetPin")
    public ResponseEntity<Response> resetCustomerPIN(@Valid @RequestBody SignInDto login) {

        int response_status = account.ResetCustomerPIN(login);

        if(response_status == 1) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer pin has been reset successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else if(response_status == 2) {

            Response response = new Response();
            response.setResponseCode(303);
            response.setResponseMessage("Account does not exists");

            return new ResponseEntity(response, HttpStatus.OK);

        }
        else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request, please retry");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }//end of service


    //service to reset pin
    @PostMapping("/changePinNumber")
    public ResponseEntity changeCustomerPIN(@Valid @RequestBody SignInDto login) {

        if(account.ChangePINNumber(login)) {

            Response response = new Response();
            response.setResponseCode(200);
            response.setResponseMessage("Customer pin has been changed successfully");

            return new ResponseEntity(response, HttpStatus.OK);

        }else {
            Response response = new Response();
            response.setResponseCode(404);
            response.setResponseMessage("Unable to process your request");

            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }//end of service

    // service to fetch app version
    @PostMapping("/updateAppVersion")
    public ResponseEntity<AppVersion> updateAppVersion(@Valid @RequestBody AppVersionDetailsDto request) throws Exception {

        AppVersion appVersion = account.updateAppVersion(request);

        return new ResponseEntity(appVersion, HttpStatus.OK);
    }


    //service to fetch profile
    @GetMapping("/checkAppVersion")
    public ResponseEntity fetchCheckAppVersion(@RequestParam String platform) {

        AppVersion appVersion = new AppVersion();

        appVersion = account.validateAppVersion(platform);

        return new ResponseEntity(appVersion, HttpStatus.OK);
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

        Response response = new Response();

        customerEntry = account.AuthenticateCustomerAccount(login);

        if(customerEntry != null) {

            //get company loan profile
            emprofile = account.FetchEmployerLoanProfile(customerEntry.getCUSTOMER_ENTRY_ID());
            customerEmployerDetails = account.FetchCustomerEmployerLoanProfile(customerEntry.getCUSTOMER_ENTRY_ID());
            loanDetails = account.fetchCustomerLoanDetails(customerEntry.getCUSTOMER_ENTRY_ID());



            response.setResponseCode(200);
            response.setResponseMessage("Customer Found!");

            responseEntry.setResponse(response);
            responseEntry.setCustomer(customerEntry);
            responseEntry.setActiveLoan(loanDetails);
            responseEntry.setCustomerEmployerDetails(customerEmployerDetails);
            responseEntry.setEmployerloanProfile(emprofile);

            /*
            Double annualSalary = customerEmployerDetails.getANNUAL_SALARY();
            Double limitPercent = emprofile.getLOAN_LIMIT_PERCENT();
            Double loanInterest = emprofile.getLOAN_INTEREST_RATE();
            Double ApprovedLoanAmount;

            Double monthlyApprovedLoan = (((annualSalary / 12) * loanInterest) / 100) * 6;
            Double averageInterest = 1 + (6 * (loanInterest /100));

            ApprovedLoanAmount = monthlyApprovedLoan / averageInterest;
             */

            if(customerEmployerDetails != null) {

                String ApprovedLoanAmount = account.CalculatePreApprovedAmount(
                        customerEmployerDetails.getANNUAL_SALARY().toString(),
                        "1",
                        emprofile.getLOAN_INTEREST_RATE().toString(),
                        emprofile.getLOAN_LIMIT_PERCENT().toString());

                responseEntry.setLoanPreApprovedAmount(Double.parseDouble(ApprovedLoanAmount));

            }

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        responseEntry.setResponse(response);

        response.setResponseCode(401);
        response.setResponseMessage("Customer not found!");

        return new ResponseEntity(responseEntry, HttpStatus.OK);
    }

}
