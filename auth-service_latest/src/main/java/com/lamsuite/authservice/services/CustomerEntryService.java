package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.exception.DocumentUploadException;
import com.lamsuite.authservice.model.*;
import com.lamsuite.authservice.repository.CustomerEntry;
import com.lamsuite.authservice.rowMappers.*;
import com.lamsuite.authservice.rowMappers.Loan.CustomerLoanDetailsWrapper;
import com.lamsuite.authservice.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CustomerEntryService implements CustomerEntry<Entry> {

    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/LAM_DB";
    private static final String dbUsername = "delushsa";
    private static final String dbPassword = "@Dmin123$";

    //LOGGER
    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);
    private static DataSource dataSource;

    EntryRowMapper entryRowMapper = new EntryRowMapper();

    public CustomerEntryService() {
        //dataSource = Utilities.initialDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
        dataSource = Utilities.hikariDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
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

    @Override
    public boolean ValidateCustomerRecord(ValidateCustomer customer) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*) FROM LAM_CUSTOMER_ENTRY WHERE EMAIL_ADDRESS = ? OR PHONE_NUMBER = ?";

            //insert
            Integer count =  dbCor.queryForObject(sql, new Object[] {customer.getEmailAddress(), customer.getPhoneNumber() }, Integer.class);

            if(count == 0) {
                return true;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean PostCustomerTransaction(TransactionDto transaction) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_TRANSACTIONS (TRANSACTION_ID, CUSTOMER_ID, " +
                    "TRANSACTION_AMOUNT, TRANSACTION_TYPE, SUMMARY, NARRATION, REQUEST_STATUS, " +
                    "PAYMENT_STATUS, PAYMENT_REFERENCE, PAYMENT_RESPONSE_DATE, DATE_CREATED, CREATED_BY) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

            String TRANSACTION_ID = UUID.randomUUID().toString();

            //insert
            int status = dbCor.update(sql, TRANSACTION_ID, transaction.getCustomer_id(), transaction.getAmount(),
                    transaction.getTransaction_type(), transaction.getSummary(), transaction.getNarration(),
                    transaction.getRequest_status(), transaction.getPayment_status(), transaction.getPayment_reference(),
                    transaction.getPayment_response_date(),LocalDateTime.now(), "SYSTEM");

            if (status == 1) {
                logger.info("Customer transaction posted successfully!");
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;

    }

    @Override
    public Profile FetchCustomerProfile(String CustomerID) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        Profile profile = new Profile();

        try {

            //SQL Script
            String sql = "SELECT CUSTOMER_ENTRY_ID, ACCOUNT_TYPE, USERNAME, FULL_NAME, PHONE_NUMBER, EMAIL_ADDRESS," +
                         "(SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE WHERE PROFILE_ID = E.EMPLOYER_ID)COMPANY_NAME " +
                         "FROM LAM_CUSTOMER_ENTRY C LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON C.CUSTOMER_ENTRY_ID = E.CUSTOMER_ID " +
                         "WHERE C.CUSTOMER_ENTRY_ID = ?";

            profile  =  dbCor.queryForObject(sql, new Object[]{CustomerID}, new ViewProfileRowMapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return profile;
    }

    // create customer PIN access
    private boolean createClientPINSecret(String CustomerID, String PinNumber) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL ScripT
            String sql = "INSERT INTO LAM_CUSTOMER_ACCESS (ACCESS_ID, CUSTOMER_ENTRY_ID, " +
                    "CUSTOMER_ACCESS_CODE, DATE_CREATED, CREATED_BY) VALUES (?,?,?,?,?)";

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
    public int ResetCustomerPIN(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql_check = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER_ENTRY WHERE USERNAME = ?";

            Integer count =  dbCor.queryForObject(sql_check, new Object[] { account.getUsername() }, Integer.class);

            if(count == 0) {
                return 2;
            }

            //SQL Script
            String sql = "UPDATE LAM_CUSTOMER_ACCESS SET CUSTOMER_ACCESS_CODE = ?, LAST_RESET_DATE = ?, RESET_STATUS = 1 " +
                         "WHERE CUSTOMER_ENTRY_ID = (SELECT CUSTOMER_ENTRY_ID FROM " +
                         "LAM_CUSTOMER_ENTRY WHERE USERNAME = ?)";

            int newPin = Utilities.generateNewPIN();

            String CUSTOMER_ACCESS_CODE = Utilities.hashPINSecret(String.valueOf(newPin));

            //insert
            int status = dbCor.update(sql,CUSTOMER_ACCESS_CODE, LocalDateTime.now(), account.getUsername());

            NotificationService notificationService = new NotificationService();

            if(status == 1) {

                ResetClient client = new ResetClient();
;
                String sql2 = "SELECT FULL_NAME, EMAIL_ADDRESS FROM LAM_CUSTOMER_ENTRY WHERE USERNAME = ?";

                client  =  dbCor.queryForObject(sql2, new Object[]{account.getUsername()}, new ResetClientMappers());

                assert client != null;

                notificationService.SendPasswordResetNotification(client.getFULL_NAME(), client.getEMAIL_ADDRESS(), String.valueOf(newPin));
                logger.info("PIN Password Created Successfully");
                return 1;

            }else {
                return 0;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean ChangePINNumber(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "UPDATE LAM_CUSTOMER_ACCESS SET CUSTOMER_ACCESS_CODE = ?, LAST_RESET_DATE = ?, RESET_STATUS = 0 " +
                    "WHERE CUSTOMER_ENTRY_ID = (SELECT CUSTOMER_ENTRY_ID FROM " +
                    "LAM_CUSTOMER_ENTRY WHERE USERNAME = ?)";

            String CUSTOMER_ACCESS_CODE = Utilities.hashPINSecret(String.valueOf(account.getPinNumber()));

            //insert
            int status = dbCor.update(sql,CUSTOMER_ACCESS_CODE, LocalDateTime.now(), account.getUsername());

            // NotificationService notificationService = new NotificationService();

            if(status == 1) {

                /*
                ResetClient client = new ResetClient();
                ;
                String sql2 = "SELECT FULL_NAME, EMAIL_ADDRESS FROM LAM_CUSTOMER_ENTRY WHERE USERNAME = ?";

                client  =  dbCor.queryForObject(sql2, new Object[]{account.getUsername()}, new ResetClientMappers());

                assert client != null;

                notificationService.SendPasswordResetNotification(client.getFULL_NAME(), client.getEMAIL_ADDRESS(), String.valueOf(newPin));
                */

                logger.info("PIN has been changed Successfully");
                return true;

            }else {
                return false;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    @Override
    public VerifyOTPValue CreateRegistrationOTP(ValidateCustomer details) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);
        VerifyOTPValue response = new VerifyOTPValue();

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_OTP_VERIFICATION (VERIFICATION_ID, FULL_NAME, EMAIL_ADDRESS, " +
                         "PHONE_NUMBER, OTP_VALUE, SMS, EMAIL, DATE_CREATED) VALUES" +
                         "(?,?,?,?,?,?,?,?)";

            String verificationID = UUID.randomUUID().toString();
            String otpValue = Utilities.generateOTPValue();
            Integer SMSValue = 1;
            Integer EmailValue = 1;

            //insert
            int status = dbCor.update(sql,verificationID, details.getFull_name(), details.getEmailAddress(),
                    details.getPhoneNumber(), otpValue, SMSValue, EmailValue, LocalDateTime.now());

            if(status == 1) {

                logger.info("OTP has been saved successfully");

                NotificationService notificationService = new NotificationService();

                notificationService.SendVerificationCode(details.getEmailAddress(), details.getFull_name(), otpValue);

                response.setOtp_value(otpValue);
                response.setVerificationId(verificationID);

                return response;

            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

       return response;
    }

    @Override
    public boolean VerifyRegistrationOTP(VerifyOTPResponse otpResponse) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT COUNT(*) FROM LAM_OTP_VERIFICATION WHERE " +
                    "VERIFICATION_ID = ? AND OTP_VALUE = ? AND PHONE_NUMBER = ?";

            Integer count =  dbCor.queryForObject(sql, Integer.class, otpResponse.getVerification_id(),
                    otpResponse.getOtp_value(), otpResponse.getPhoneNumber());

            if(count == 1) {

                //SQL Script
                String sql_update = "UPDATE LAM_OTP_VERIFICATION SET VERIFIED = 1, " +
                                    "DATE_VERIFIED = ? WHERE VERIFICATION_ID = ?";

                //insert
                int status = dbCor.update(sql_update, LocalDateTime.now(),  otpResponse.getVerification_id());

                return true;
            }

        }catch(DataAccessException e) {
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
                    "WHERE PROFILE_ID = E.EMPLOYER_ID)EMPLOYER_NAME, " +
                    "(SELECT SUM(REPAYMENT_AMOUNT) FROM LAM_CUSTOMER_LOAN_REPAYMENT WHERE LOAN_ID = L.LOAN_ID)LOAN_TOTAL_REPAYMENT FROM LAM_CUSTOMER_LOAN_REQUEST L LEFT JOIN " +
                    "LAM_CUSTOMER_ENTRY C ON L.CUSTOMER_ID = C.CUSTOMER_ENTRY_ID LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON " +
                    "C.CUSTOMER_ENTRY_ID = E.CUSTOMER_ID WHERE L.CUSTOMER_ID = ? GROUP BY L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, " +
                    "L.LOAN_STATUS, L.DATE_CREATED, L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT,L.TOTAL_REPAYMENT, " +
                    "L.INTEREST_RATE, L.LOAN_TENOR, E.EMPLOYER_ID;";

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

            if(count == 3) {
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
    public boolean UploadCustomerDocuments(MultipartFile file, String docType, String CustomerID) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {

            if(fileName.contains("..")) {
                throw new DocumentUploadException("Filename contains invalid path sequence " + fileName);
            }

            int fileLength = file.getBytes().length;

            if(fileLength> (1024 * 1024)) {
                throw new DocumentUploadException("File size exceeds maximum limit");
            }

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_DOCUMENTS (DOCUMENT_ID, DOCUMENT_TYPE, CUSTOMER_ID, FILE_NAME, FILE_TYPE, DOCUMENT_DATA, " +
                    "DATE_CREATED, CREATED_BY) VALUES " +
                    "(?,?,?,?,?,?,?,?)";

            String DOCUMENT_ID = UUID.randomUUID().toString();
            String FileType = file.getContentType();
            byte[] DocumentData = file.getBytes();

            //insert
            int status = dbCor.update(sql, DOCUMENT_ID, docType, CustomerID, fileName, FileType, DocumentData,
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
            throw new DocumentUploadException("File size exceeds maximum limit");
        }

        return false;
    }
    // end of service


    public String CalculatePreApprovedAmount(String annualSalary, String loanTenor, String loanInterest, String loanLimitPercent) {

        DecimalFormat df = new DecimalFormat("#.##");

        double annual_salary = Double.parseDouble(annualSalary);
        int loan_tenor = Integer.parseInt(loanTenor);
        double loan_interest = (Double.parseDouble(loanInterest)/100);

        double monthly_salary = annual_salary / 12;

        double max_monthly_salary = Double.parseDouble(loanLimitPercent) / 100;

        double max_monthly_repayment = max_monthly_salary * monthly_salary;

        double pre_apprv_one = max_monthly_repayment * loan_tenor;

        double pre_apprv_two = 1 + (loan_tenor * loan_interest);

        double preApprovedAmount = pre_apprv_one / pre_apprv_two;

        return df.format(preApprovedAmount);
    }

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

    @Override
    public AppVersion validateAppVersion(String platform) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //fetch data
            String sql_fetch = "SELECT PLATFORM_ID, PLATFORM, LATEST_VERSION, " +
                    "MINIMUM_VERSION, BUILD_NO, DATE_CREATED, DATE_UPDATED, " +
                    "STATUS FROM LAM_APP_VERSION WHERE PLATFORM = ?";

            AppVersion appVersion = new AppVersion();

            appVersion  =  dbCor.queryForObject(sql_fetch, new Object[]{platform}, new AppVersionMapper());

            return appVersion;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Override
    public AppVersion updateAppVersion(AppVersionDetailsDto request) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql_check = "SELECT COUNT(*)COUNT FROM LAM_APP_VERSION WHERE " +
                    "PLATFORM = ?";

            Integer count =  dbCor.queryForObject(sql_check, Integer.class, request.getPlatform());


            if(count == 0) {

                //SQL Script
                String sql = "INSERT INTO LAM_APP_VERSION (PLATFORM_ID, PLATFORM, LATEST_VERSION, MINIMUM_VERSION, " +
                        "BUILD_NO, DATE_CREATED) VALUES (?,?,?,?,?,?)";

                String appId = UUID.randomUUID().toString();

                //insert
                int status = dbCor.update(sql, appId, request.getPlatform(), request.getLatestVersion(), request.getMinimumVersion(), request.getBuildNo(),
                        LocalDateTime.now());

            }else {

                //SQL Script
                String sql = "UPDATE LAM_APP_VERSION SET LATEST_VERSION = ?, MINIMUM_VERSION = ?, " +
                        "BUILD_NO = ?, DATE_UPDATED = ? WHERE PLATFORM = ?";

                //insert
                int status = dbCor.update(sql, request.getLatestVersion(), request.getMinimumVersion(), request.getBuildNo(),
                        LocalDateTime.now(), request.getPlatform());
            }

            //fetch data
            String sql_fetch = "SELECT PLATFORM_ID, PLATFORM, LATEST_VERSION, " +
                    "MINIMUM_VERSION, BUILD_NO, DATE_CREATED, DATE_UPDATED, " +
                    "STATUS FROM LAM_APP_VERSION WHERE PLATFORM = ?";

            AppVersion appVersion = new AppVersion();

            appVersion  =  dbCor.queryForObject(sql_fetch, new Object[]{request.getPlatform()}, new AppVersionMapper());

            return appVersion;


        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }
    // end of service

    // service to update client record
    @Override
    public String UpdatePersonalData(PersonalDataUpdateDto record) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            boolean checkStatus = CheckCustomerExists(record.getEmailAddress(), record.getPhone_number());

            //SQL Script
            String sqlcount = "SELECT COUNT(*) FROM LAM_CUSTOMER";

            Integer account_count =  dbCor.queryForObject(sqlcount, Integer.class);

            if(!checkStatus) {

                //SQL Script
                String sql = "INSERT INTO LAM_CUSTOMER (CUSTOMER_ID, CUSTOMER_NO, ACCOUNT_NO, SCHEME_TYPE, KYC_STATUS, LAST_NAME," +
                                                        "FIRST_NAME, OTHER_NAME, DATE_OF_BIRTH, GENDER, PLACE_OF_BIRTH, PHONE_NUMBER, " +
                                                        "EMAIL_ADDRESS,STATE_OF_ORIGIN, NATIONALITY, ADDRESS, AREA_LOCALITY, STATE, " +
                                                        "DATE_CREATED, CREATED_BY, RECORD_SOURCE) VALUES " +
                                                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                Integer newAccountCount = account_count + 1;
                String CUSTOMER_NO = String.valueOf(Utilities.generateCustomerNumber());
                String AccountNo = Utilities.generateAccountNumber(newAccountCount);
                String Scheme_type = "Loan";
                String KYC_STATUS = "Pending";

                //insert
                int status = dbCor.update(sql,record.getCustomer_id(), CUSTOMER_NO, AccountNo, Scheme_type, KYC_STATUS, record.getLastname(),
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

    // create send OTP Validation
    // end of service

    // service to authenticate customer
    @Override
    public Entry AuthenticateCustomerAccount(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        Entry customerEntry = null;

        try {

            //SQL Script
           String sql = "SELECT C.*, A.RESET_STATUS FROM LAM_CUSTOMER_ENTRY C LEFT JOIN LAM_CUSTOMER_ACCESS " +
                        "A ON C.CUSTOMER_ENTRY_ID = A.CUSTOMER_ENTRY_ID WHERE C.USERNAME = ?";

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

                    UpdateLastLoginDate(account);

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

    @Override
    public List<Transaction> FetchCustomerTransactions(String CustomerID) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "SELECT TRANSACTION_ID, CUSTOMER_ID, TRANSACTION_AMOUNT, " +
                         "TRANSACTION_TYPE, NARRATION, DATE_CREATED, CREATED_BY FROM " +
                         "LAM_CUSTOMER_TRANSACTIONS WHERE CUSTOMER_ID = ?";

            //spool;
            return dbCor.query(sql, new Object[]{CustomerID}, new TransactionDetailsRowMapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    // update customer pin number
    private void UpdateLastLoginDate(SignInDto account) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "UPDATE LAM_CUSTOMER_ENTRY SET LAST_LOGIN_DATE = ? WHERE USERNAME = ?";

            //insert
            int status = dbCor.update(sql, LocalDateTime.now(), account.getUsername());

        }catch(Exception e) {
            logger.error(e.getMessage());
        }
    }
    // end of service
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
