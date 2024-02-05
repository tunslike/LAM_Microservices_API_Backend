package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.request.CustomerDto;
import com.lamsuite.authservice.dto.request.CustomerRecordDto;
import com.lamsuite.authservice.dto.request.SignInDto;
import com.lamsuite.authservice.model.Entry;
import com.lamsuite.authservice.repository.CustomerEntry;
import com.lamsuite.authservice.rowMappers.EntryRowMapper;
import com.lamsuite.authservice.utilities.Utilities;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerEntryService implements CustomerEntry<Entry> {

    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/LAM_DB";
    private static final String dbUsername = "root";
    private static final String dbPassword = "";

    //LOGGER
    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);
    private static DataSource dataSource;

    EntryRowMapper entryRowMapper = new EntryRowMapper();

    public CustomerEntryService() {
        dataSource = Utilities.initialDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
    }

    //create customer account
    @Override
    public boolean CreateCustomerAccount(CustomerDto customer) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_ENTRY( CUSTOMER_ENTRY_ID, ACCOUNT_TYPE, USERNAME, " +
                    "FULL_NAME, PHONE_NUMBER, EMAIL_ADDRESS, DATE_CREATED, CREATED_BY ) VALUES " +
                    "(?,?,?,?,?,?,?,?)";

            String Customer_ID = UUID.randomUUID().toString();

            //insert
            int status = dbCor.update(sql, Customer_ID, customer.getAccount_type(), customer.getEmailAddress(), customer.getFull_name(),
                    customer.getPhoneNumber(), customer.getEmailAddress(), LocalDateTime.now(),
                    "SYSTEM");

            if(status == 1) {
                //create password
                createClientPINSecret(Customer_ID);
                logger.info("New Customer Created Successfully");
                return true;
            }else{
                return false;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }// end of service

    // create customer PIN access
    private boolean createClientPINSecret(String CustomerID) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_ACCESS (ACCESS_ID, CUSTOMER_ENTRY_ID, CUSTOMER_ACCESS_CODE, " +
                          "DATE_CREATED, CREATED_BY) VALUES (?, ?, ?, ?, ?)";

            String ACCESS_ID = UUID.randomUUID().toString();
            String CUSTOMER_ID = CustomerID;
            String CUSTOMER_ACCESS_CODE = Utilities.hashPINSecret("123456");

            //insert
            int status = dbCor.update(sql, ACCESS_ID, CUSTOMER_ID, CUSTOMER_ACCESS_CODE, LocalDateTime.now(),"SYSTEM");

            if(status == 1) {
                logger.info("PIN Password Created Successfully");
                return true;
            }else {
                return false;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to check that customer exist
    private boolean CheckCustomerExists(String email, String phonenumber)  {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER WHERE " +
                         "EMAIL_ADDRESS = ? AND PHONE_NUMBER = ?";

            Integer count =  dbCor.queryForObject(sql, Integer.class, new Object[] { email,phonenumber });

            if(count == 1) {
                return true;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }


        return false;
    }
    // end of service

    // service to update client record
    public boolean UpdateCustomerRecord(CustomerRecordDto record) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            boolean checkStatus = CheckCustomerExists(record.getEmailAddress(), record.getPhone_number());

            if(!checkStatus) {

                //SQL Script
                String sql = "INSERT INTO LAM_CUSTOMER (CUSTOMER_ID, CUSTOMER_NO, SCHEME_TYPE, KYC_STATUS, LAST_NAME," +
                                                        "FIRST_NAME, OTHER_NAME, DATE_OF_BIRTH, GENDER, PLACE_OF_BIRTH, PHONE_NUMBER, " +
                                                        "EMAIL_ADDRESS,STATE_OF_ORIGIN, NATIONALITY, ADDRESS, AREA_LOCALITY, STATE, " +
                                                        "DATE_CREATED, CREATED_BY, RECORD_SOURCE) VALUES " +
                                                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                String CUSTOMER_ID = UUID.randomUUID().toString();
                String CUSTOMER_NO = "12345678";
                String Scheme_type = "";
                String KYC_STATUS = "";

                //insert
                int status = dbCor.update(sql,CUSTOMER_ID, CUSTOMER_NO, Scheme_type, KYC_STATUS, record.getLastname(),
                        record.getFirstname(), record.getOther_name(), record.getDate_of_birth(), record.getGender(), record.getPlace_of_birth(),
                        record.getPhone_number(), record.getEmailAddress(), record.getState_of_origin(), record.getNationality(), record.getAddress(),
                        record.getArea_location(), record.getState(), LocalDateTime.now(),"SYSTEM", "MOBILE_APP");

                if(status == 1) {

                    logger.info("Customer record updated Successfully");
                    logger.info("Creating record for Employer details");
                    return true;

                }else return false;

            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to authenticate customer
    @Override
    public Entry AuthenticateCustomerAccount(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        Entry customerEntry = null;

        try {

            //SQL Script
           String sql = "SELECT * FROM LAM_CUSTOMER_ENTRY WHERE USERNAME = ?";

            //spool;
            customerEntry = dbCor.queryForObject(sql, new Object[]{account.getUsername()}, new EntryRowMapper());

            if(customerEntry != null) {

                //validate passcode
                if(ValidateCustomerEntryCode(customerEntry.getCUSTOMER_ENTRY_ID(), account.getPinNumber()))
                    return customerEntry;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private boolean ValidateCustomerEntryCode(String CustomerEntryID, String plainPassword) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT CUSTOMER_ACCESS_CODE FROM LAM_CUSTOMER_ACCESS WHERE CUSTOMER_ENTRY_ID = ?";

            //spool;
            String hashPassword = (String) dbCor.queryForObject(sql, new Object[] { CustomerEntryID }, String.class);

            return Utilities.validatePINNumber(plainPassword, hashPassword);

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

}
