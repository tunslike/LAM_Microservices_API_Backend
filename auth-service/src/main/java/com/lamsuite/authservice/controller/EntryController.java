package com.lamsuite.authservice.controller;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.Response;
import com.lamsuite.authservice.dto.request.CustomerDto;
import com.lamsuite.authservice.dto.request.SignInDto;
import com.lamsuite.authservice.model.Entry;
import com.lamsuite.authservice.services.CustomerEntryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import jakarta.validation.Valid;

@CrossOrigin("http://localhost:8081")
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

    @PostMapping("/login")
    public ResponseEntity authenticateCustomer(@Valid @RequestBody SignInDto login) {
        Entry customerEntry = new Entry();
        LoginResponse responseEntry = new LoginResponse();

        customerEntry = account.AuthenticateCustomerAccount(login);

        if(customerEntry != null) {

            Response response = new Response();

            response.setResponseCode(200);
            response.setResponseMessage("Customer Found!");

            responseEntry.setResponse(response);
            responseEntry.setCustomer(customerEntry);

            return new ResponseEntity(responseEntry, HttpStatus.OK);
        }

        return new ResponseEntity(responseEntry, HttpStatus.BAD_REQUEST);
    }

}
