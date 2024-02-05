package com.lamsuite.authservice.repository;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.request.CustomerDto;
import com.lamsuite.authservice.dto.request.CustomerRecordDto;
import com.lamsuite.authservice.dto.request.SignInDto;
import com.lamsuite.authservice.model.Entry;

public interface CustomerEntry<T> {

    //create customer account
    boolean CreateCustomerAccount(CustomerDto customer);

    //login customer account
    Entry AuthenticateCustomerAccount(SignInDto account);

    boolean UpdateCustomerRecord(CustomerRecordDto record);

}
