package com.lamsuite.authservice.repository;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.EmployerProfile;
import com.lamsuite.authservice.model.Entry;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerEntry<T> {

    //create customer account
    boolean CreateCustomerAccount(CustomerDto customer);

    //login customer account
    Entry AuthenticateCustomerAccount(SignInDto account);

    String UpdatePersonalData(PersonalDataUpdateDto record);

    boolean UpdateEmployerData(EmployerDataUpdateDto employerRecord);

    boolean UpdateNOKData(NOKDataUpdateDto nokRecord);

    boolean UploadCustomerDocuments(MultipartFile file) throws Exception;

    List<EmployerProfile> FetchEmployerProfiles();

}
