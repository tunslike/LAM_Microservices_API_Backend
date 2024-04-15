package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.EntryResponse;
import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.dto.LoginResponse;
import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.*;
import com.lamsuite.authservice.repository.CustomerEntry;
import com.lamsuite.authservice.rowMappers.*;
import com.lamsuite.authservice.rowMappers.Loan.CustomerLoanDetailsWrapper;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
                    "FULL_NAME, PHONE_NUMBER, EMAIL_ADDRESS, EMPLOYER_PROFILE_ID, DATE_CREATED, CREATED_BY ) VALUES " +
                    "(?,?,?,?,?,?,?,?,?)";

            String Customer_ID = UUID.randomUUID().toString();

            //insert
            int status = dbCor.update(sql, Customer_ID, customer.getAccount_type(), customer.getEmailAddress(), customer.getFull_name(),
                    customer.getPhoneNumber(), customer.getEmailAddress(), customer.getEmployer_profile_id(), LocalDateTime.now(),
                    "SYSTEM");

            if(status == 1) {
                //create password
                createClientPINSecret(Customer_ID, customer.getPinNumber());

                // send notification
                NotificationService notificationService = new NotificationService();

                notificationService.SendAccountRegistrationNotification(customer.getEmailAddress(), customer.getPinNumber(),
                        customer.getFull_name());

                logger.info("[" +customer.getFull_name() + "] New Customer Created Successfully");
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
    private boolean createClientPINSecret(String CustomerID, String PinNumber) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_ACCESS (ACCESS_ID, CUSTOMER_ENTRY_ID, CUSTOMER_ACCESS_CODE, " +
                          "DATE_CREATED, CREATED_BY) VALUES (?, ?, ?, ?, ?)";

            String ACCESS_ID = UUID.randomUUID().toString();
            String CUSTOMER_ID = CustomerID;
            String CUSTOMER_ACCESS_CODE = Utilities.hashPINSecret(PinNumber);

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

    // update customer pin number
    public boolean ResetCustomerPIN(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "UPDATE LAM_CUSTOMER_ACCESS SET CUSTOMER_ACCESS_CODE = ?, LAST_RESET_DATE = ?" +
                         "WHERE CUSTOMER_ENTRY_ID = (SELECT CUSTOMER_ENTRY_ID FROM " +
                         "LAM_CUSTOMER_ENTRY WHERE USERNAME = ?)";

            String CUSTOMER_ACCESS_CODE = Utilities.hashPINSecret("123456");

            //insert
            int status = dbCor.update(sql,CUSTOMER_ACCESS_CODE, LocalDateTime.now(), account.getUsername());

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

    // service to return employer profile ids
    public List<EmployerProfile> FetchEmployerProfiles() {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT * FROM LAM_COMPANY_PROFILE WHERE STATUS = 1";

            return dbCor.query(sql,new EmployerProfileMapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Override
    public LoanDetails fetchCustomerLoanDetails(String CustomerID) throws Exception {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        LoanDetails loanDetails = new LoanDetails();

        try {

            //SQL Script
            String sql = "SELECT L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, " +
                    "L.DATE_CREATED, L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT, " +
                    "L.TOTAL_REPAYMENT, L.INTEREST_RATE, L.LOAN_TENOR, (SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE " +
                    "WHERE PROFILE_ID = E.EMPLOYER_ID)EMPLOYER_NAME FROM LAM_CUSTOMER_LOAN_REQUEST L LEFT JOIN " +
                    "LAM_CUSTOMER_ENTRY C ON L.CUSTOMER_ID = C.CUSTOMER_ENTRY_ID LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON " +
                    "C.CUSTOMER_ENTRY_ID = E.CUSTOMER_ID WHERE L.CUSTOMER_ID = ?";

            loanDetails  =  dbCor.queryForObject(sql, new Object[]{CustomerID}, new CustomerLoanDetailsWrapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return loanDetails;
    }

    // service to check that customer exists
    private boolean CheckCustomerDocuments(String CustomerID)  {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER_DOCUMENTS WHERE CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql, new Object[] { CustomerID }, Integer.class);

            if(count == 1) {
                return true;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to check that customer exists
    private boolean CheckCustomerEmployer(String CustomerID)  {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER_EMPLOYERS WHERE CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql, new Object[] { CustomerID }, Integer.class);

            if(count == 1) {
                return true;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to check that customer exists
    private boolean CheckCustomerNOK(String CustomerID)  {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_NEXT_OF_KIN WHERE CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql, new Object[] { CustomerID }, Integer.class);

            if(count == 1) {
                return true;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to check that customer exists
    private boolean CheckCustomerBiodata(String CustomerID)  {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER WHERE CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql,new Object[] { CustomerID }, Integer.class);

            if(count == 1) {
                return true;
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to check that customer exists
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

    // service to update employer record
    @Override
    public boolean UpdateEmployerData(EmployerDataUpdateDto employerRecord) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

                //SQL Script
                String sql = "INSERT INTO LAM_CUSTOMER_EMPLOYERS (CUSTOMER_ID, EMPLOYER_ID, SECTOR, GRADE_LEVEL, SERVICE_LENGTH," +
                        "STAFF_ID_NUMBER, SALARY_PAYMENT_DATE, ANNUAL_SALARY) VALUES " +
                        "(?,?,?,?,?,?,?,?)";

                //insert
                int status = dbCor.update(sql,employerRecord.getCustomer_id(), employerRecord.getEmployerProfileID(),
                                        employerRecord.getSector(), employerRecord.getGrade_level(), employerRecord.getService_length(),
                                        employerRecord.getStaff_id_number(), employerRecord.getSalary_payment_date(),
                                        employerRecord.getAnnual_salary());

                if(status == 1) {

                    logger.info("Employer data record updated Successfully");

                    return true;

                }else return false;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to upload customer document
    @Override
    public boolean UploadCustomerDocuments(MultipartFile file) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {

            if(fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }

            if(file.getBytes().length > (1024 * 1024)) {
                throw new Exception("File size exceeds maximum limit");
            }

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_DOCUMENTS (DOCUMENT_ID, CUSTOMER_ID, FILE_NAME, FILE_TYPE, DOCUMENT_DATA, " +
                    "DATE_CREATED, CREATED_BY) VALUES " +
                    "(?,?,?,?,?,?,?)";

            String DOCUMENT_ID = UUID.randomUUID().toString();
            String CustomerID = "";
            String FileType = file.getContentType();
            byte[] DocumentData = file.getBytes();

            //insert
            int status = dbCor.update(sql, DOCUMENT_ID, CustomerID, fileName, FileType, DocumentData,
                    LocalDateTime.now(), "SYSTEM");

            if(status == 1) {

                logger.info("Document saved Successfully");

                return true;

            }else return false;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(file.getSize());
        }catch(Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }

        return false;
    }

    // end of service

    // service to update nok record
    @Override
    public boolean UpdateNOKData(NOKDataUpdateDto nokRecord) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_NEXT_OF_KIN (CUSTOMER_ID, NOK_LAST_NAME, NOK_FIRST_NAME, NOK_RELATIONSHIP, " +
                    "NOK_GENDER, NOK_PHONE, NOK_EMAIL_ADDRESS, NOK_ADDRESS, NOK_AREA_LOCALITY, NOK_STATE, " +
                    "NOK_DATE_CREATED, NOK_CREATED_BY) VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?,?)";

            //insert
            int status = dbCor.update(sql,nokRecord.getCustomer_id(), nokRecord.getNok_lastname(), nokRecord.getNok_firstname(), nokRecord.getNok_relationship(),
                                    nokRecord.getNok_gender(), nokRecord.getNok_phone(), nokRecord.getNok_email(),
                                    nokRecord.getNok_address(), nokRecord.getNok_areaLocality(), nokRecord.getNok_state(),
                                    LocalDateTime.now(), "SYSTEM");

            if(status == 1) {

                logger.info("NOK data record updated Successfully");

                return true;

            }else return false;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
    // end of service

    // service to update client record
    @Override
    public String UpdatePersonalData(PersonalDataUpdateDto record) {
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

                String CUSTOMER_NO = String.valueOf(Utilities.generateCustomerNumber());
                String Scheme_type = "Loan";
                String KYC_STATUS = "Pending";

                //insert
                int status = dbCor.update(sql,record.getCustomer_id(), CUSTOMER_NO, Scheme_type, KYC_STATUS, record.getLastname(),
                        record.getFirstname(), record.getOther_name(), record.getDate_of_birth(), record.getGender(), record.getPlace_of_birth(),
                        record.getPhone_number(), record.getEmailAddress(), record.getState_of_origin(), record.getNationality(), record.getAddress(),
                        record.getArea_location(), record.getState(), LocalDateTime.now(),"SYSTEM", "MOBILE_APP");

                if(status == 1) {

                    logger.info("Customer record updated Successfully");

                    return record.getCustomer_id();

                }else return null;

            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }
    // end of service

    // service to load employer loan profile
    public CustomerEmployerDetails FetchCustomerEmployerLoanProfile(String CustomerID) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);
        CustomerEmployerDetails employerDetails = null;

        try {

            //SQL Script
            String sql = "SELECT SECTOR, GRADE_LEVEL, SERVICE_LENGTH, STAFF_ID_NUMBER, " +
                    "SALARY_PAYMENT_DATE, ANNUAL_SALARY FROM LAM_CUSTOMER_EMPLOYERS WHERE CUSTOMER_ID = ?";

            //spool;
            employerDetails = dbCor.queryForObject(sql, new Object[]{CustomerID}, new CustomerEmployerDetailsMapper());

            return employerDetails;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return employerDetails;
    }

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
                if(ValidateCustomerEntryCode(customerEntry.getCUSTOMER_ENTRY_ID(), account.getPinNumber())) {

                    boolean checkStatus = CheckCustomerBiodata(customerEntry.getCUSTOMER_ENTRY_ID());
                    boolean checkEmployer = CheckCustomerEmployer(customerEntry.getCUSTOMER_ENTRY_ID());
                    boolean checkNOK = CheckCustomerNOK(customerEntry.getCUSTOMER_ENTRY_ID());
                    boolean checkDocument = CheckCustomerDocuments(customerEntry.getCUSTOMER_ENTRY_ID());

                    customerEntry.setIS_RECORD_FOUND(checkStatus);
                    customerEntry.setIS_NOK_FOUND(checkNOK);
                    customerEntry.setIS_DOCUMENT_FOUND(checkDocument);
                    customerEntry.setIS_EMPLOYER_FOUND(checkEmployer);

                    return customerEntry;
                }else{
                    return null;
                }


            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    public EmployerLoanProfile FetchEmployerLoanProfile(String CustomerID) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);
        EmployerLoanProfile empProfile = null;
        try {

            //SQL Script
            String sql = "SELECT S.COMPANY_PROFILE_ID, S.NO_OF_STAFF, S.LOAN_LIMIT_PERCENT, " +
                         "S.LOAN_INTEREST_RATE, S.LOAN_TENOR, S.REPAYMENT_STRUCTURE, S.DATE_CREATED " +
                         "FROM LAM_COMPANY_LOAN_SETUP S LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON S.COMPANY_PROFILE_ID = " +
                         "E.EMPLOYER_ID LEFT JOIN LAM_CUSTOMER C ON E.CUSTOMER_ID = C.CUSTOMER_ID WHERE C.CUSTOMER_ID = ?";

            //spool;
            empProfile = dbCor.queryForObject(sql, new Object[]{CustomerID}, new EmployerLoanProfileRowMapper());

            return empProfile;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return empProfile;
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
