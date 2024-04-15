package com.lamsuite.authservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);

    private static final String notification_api_key = "6A6C0FB5B774E14CB03683C6F181F8A47981D3EF876F82D47A45A74ECB2190440B8387B5477B72FAD627E2E8BFCD2DB4";

    private static final String notification_sender_FromName = "Finserve Investment Limited";

    private static final String notification_sender_FromAddress = "noreply@finserveinvestment.com";

    private static final String notification_sender_sendBaseUrl = "https://api.elasticemail.com/v2/email/send?apikey=";

    public NotificationService () {

    }

    public void SendStaffLoanVerificationNotification(String EmailAddress, String staffPhoneNumber, String staffEmployer,
                                                      String staffFullName, String verificationLink,
                                                      String loanAmount, String loanTenor, String loanInterest,
                                                      String loanRepayment, String employerContactName) {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "Staff Loan Request Verification";
        String APIKey = notification_api_key;
        String templateName = "Finserve_Loan_Verification";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=" + EmailAddress + "&merge_employerContactName=" + employerContactName +
                    "&merge_staffFullName=" + staffFullName + "&merge_staffPhoneNumber=" + staffPhoneNumber + "&merge_staffEmployer=" +
                    staffEmployer + "&merge_loanAmount=" + loanAmount + "&merge_loanTenor=" + loanTenor + "&merge_loanInterest=" +
                    loanInterest + "&merge_loanRepayment=" + loanRepayment + "&merge_verificationLink=" + verificationLink;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    // end of service

    // service to send notification
    public void SendTestEmail() {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "New Account Registration";
        String APIKey = notification_api_key;
        String templateName = "Finserve_New_Registration";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=tunslike@gmail.com";

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }// end of service

    // service to send account registration
    public void SendAccountRegistrationNotification(String EmailAddress, String password, String firstName) {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "New Account Registration";
        String APIKey = notification_api_key;
        String templateName = "Finserve_New_Registration";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=" + EmailAddress + "&merge_clientName=" + firstName +
                    "&merge_accountUsername=" + EmailAddress + "&merge_accountPinNumber=" + password;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    // end of service
}
