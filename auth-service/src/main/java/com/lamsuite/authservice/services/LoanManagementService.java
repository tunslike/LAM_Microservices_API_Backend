package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.Loan.BankTransferDetails;
import com.lamsuite.authservice.model.Loan.CustomerLoanSummary;
import com.lamsuite.authservice.model.Loan.LoanSummaryBreakdown;
import com.lamsuite.authservice.model.LoanDetails;
import com.lamsuite.authservice.repository.LoanManagement;
import com.lamsuite.authservice.rowMappers.CustomerEmployerDetailsMapper;
import com.lamsuite.authservice.rowMappers.Loan.AccountDetailsWrapper;
import com.lamsuite.authservice.rowMappers.Loan.BankTransferDetailsWrapper;
import com.lamsuite.authservice.rowMappers.Loan.CustomerLoanDetailsWrapper;
import com.lamsuite.authservice.utilities.AesGcmCryptor;
import com.lamsuite.authservice.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LoanManagementService implements LoanManagement {

    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/LAM_DB";
    private static final String dbUsername = "root";
    private static final String dbPassword = "";

    //LOGGER
    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);
    private static DataSource dataSource;

    @Value("${loanService.verificationURL}")
    private String verificationUrl;

    public LoanManagementService() {
        dataSource = Utilities.initialDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
    }

    @Override
    public AccountDetails AddCustomerAccountDetails(CustomerAccountDetailsDto accountDetails) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        AccountDetails details = new AccountDetails();

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_BANK_ACCOUNTS (BANK_ACCOUNT_ID, CUSTOMER_ID, BANK_NAME, " +
                    "BANK_ID, ACCOUNT_NUMBER, ACCOUNT_NAME, DATE_CREATED, CREATED_BY) VALUES " +
                    "(?,?,?,?,?,?,?,?)";

            String BANK_ACCOUNT_ID = UUID.randomUUID().toString();

            //insert
            int status = dbCor.update(sql, BANK_ACCOUNT_ID, accountDetails.getCustomerID(),
                    accountDetails.getBankName(), accountDetails.getBankID(), accountDetails.getAccountNumber(),
                    accountDetails.getAccountName(), LocalDateTime.now(),"SYSTEM");

            if(status == 1) {

                logger.info("Account Details Created Successfully");

                String sql_details = "SELECT BANK_ACCOUNT_ID, BANK_NAME, ACCOUNT_NUMBER, ACCOUNT_NAME, " +
                        "DATE_CREATED, IS_ACTIVE FROM LAM_CUSTOMER_BANK_ACCOUNTS WHERE CUSTOMER_ID = ?";

                details = dbCor.queryForObject(sql_details, new Object[]{accountDetails.getCustomerID()}, new AccountDetailsWrapper());

                return details;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return details;
    }

    @Override
    public AccountDetails FetchCustomerAccountDetails(GenerateTokenDto customer) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        AccountDetails accountDetails = null;

        try {

            //SQL Script
            String sql = "SELECT BANK_ACCOUNT_ID, BANK_NAME, ACCOUNT_NUMBER, ACCOUNT_NAME, " +
                         "DATE_CREATED, IS_ACTIVE FROM LAM_CUSTOMER_BANK_ACCOUNTS WHERE CUSTOMER_ID = ?";

            accountDetails = dbCor.queryForObject(sql, new Object[]{customer.getCustomerID()}, new AccountDetailsWrapper());

            return accountDetails;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Override
    public CustomerLoanSummary CalculateAmortizeSchedule(CalculateLoanSummaryDto loanDetails) {

        DecimalFormat df = new DecimalFormat("#.##");

        double loanAmount = Double.parseDouble(loanDetails.getLoanAmount());
        double annualInterestRate = Double.parseDouble(loanDetails.getLoanInterest()) / 100;
        int loanTermMonths = Integer.parseInt( loanDetails.getLoanTenor());

        CustomerLoanSummary loanSummary = new CustomerLoanSummary();

        double monthlyInterestRate = annualInterestRate / 12;
        double monthlyPayment = (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -loanTermMonths));

        loanSummary.setMonthly_repayment(df.format(monthlyPayment));

        //DecimalFormat df = new DecimalFormat("#.##");

        /*
        System.out.println("Monthly Payment: $" + df.format(monthlyPayment));
        System.out.println("Amortization Schedule:");
        System.out.println("Month\t\tPrincipal\t\tInterest\t\tRemaining Balance");
       */

        List<LoanSummaryBreakdown> breakdowns = new ArrayList<>();

        double totalRepayment = 0;

        double remainingBalance = loanAmount;

        for (int month = 1; month <= loanTermMonths; month++) {

            double interest = remainingBalance * monthlyInterestRate;
            double principal = monthlyPayment - interest;
            remainingBalance -= principal;

            LoanSummaryBreakdown breakdown = new LoanSummaryBreakdown();

            breakdown.setBalance(df.format(remainingBalance));
            breakdown.setInterest(df.format(interest));
            breakdown.setPrincipal(df.format(principal));
            breakdown.setMonth(df.format(month));

            breakdowns.add(breakdown);

            totalRepayment += (principal + interest);

            //System.out.println(month + "\t\t$" + df.format(principal) + "\t\t$" + df.format(interest) + "\t\t$" + df.format(remainingBalance));
        }

        loanSummary.setRepayment_schedule(breakdowns);
        loanSummary.setTotal_repayment(df.format(totalRepayment));

        return loanSummary;
    }

    @Override
    public String VerifyCustomerVerification(String CipherText) throws Exception {
        AesGcmCryptor crypto = new AesGcmCryptor();
        char[] password = {'0', '3', 't', 'u', 'v'};
        return crypto.decryptString(password, CipherText);
    }

    @Override
    public LoanDetails fetchCustomerLoanDetails(FetchLoanDetailsDto loan) throws Exception {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        LoanDetails loanDetails = new LoanDetails();

        try {

            //SQL Script
            String sql = "SELECT L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, L.DATE_CREATED, " +
                    "L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT, L.TOTAL_REPAYMENT, L.INTEREST_RATE, SUM(R.REPAYMENT_AMOUNT)LOAN_TOTAL_REPAYMENT," +
                    "L.LOAN_TENOR, (SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE WHERE " +
                    "PROFILE_ID = C.EMPLOYER_PROFILE_ID)EMPLOYER_NAME FROM LAM_CUSTOMER_LOAN_REQUEST L LEFT " +
                    "JOIN LAM_CUSTOMER_ENTRY C ON L.CUSTOMER_ID = C.CUSTOMER_ENTRY_ID LEFT JOIN LAM_CUSTOMER_LOAN_REPAYMENT R ON L.LOAN_ID = R.LOAN_ID WHERE " +
                    "L.LOAN_ID = ?";

            loanDetails  =  dbCor.queryForObject(sql, new Object[]{loan.getLoanID()}, new CustomerLoanDetailsWrapper());


        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return loanDetails;
    }

    @Override
    public BankTransferDetails fetchBankTransferDetails() throws Exception {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        BankTransferDetails bankTransferDetails = new BankTransferDetails();

        try {

            //SQL Script
            String sql = "SELECT BANK_ID, BANK_NAME, ACCOUNT_NAME, ACCOUNT_NUMBER FROM LAM_BANK_DETAILS";

            bankTransferDetails  =  dbCor.queryForObject(sql, new BankTransferDetailsWrapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return bankTransferDetails;
    }

    @Override
    public LoanDetails ValidateCustomerLoanDetails(CustomerIDdto customer) throws Exception {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        LoanDetails loanDetails = new LoanDetails();

        try {

            //SQL Script
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER_LOAN_REQUEST WHERE CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql, new Object[] { customer.getCustomerID() }, Integer.class);

            if(count == 1) {

                //SQL Script
                String sql_loan = "SELECT L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, " +
                        "L.DATE_CREATED, L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT, SUM(R.REPAYMENT_AMOUNT)LOAN_TOTAL_REPAYMENT, " +
                        "L.TOTAL_REPAYMENT, L.INTEREST_RATE, L.LOAN_TENOR, (SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE " +
                        "WHERE PROFILE_ID = E.EMPLOYER_ID)EMPLOYER_NAME FROM LAM_CUSTOMER_LOAN_REQUEST L LEFT JOIN " +
                        "LAM_CUSTOMER_ENTRY C ON L.CUSTOMER_ID = C.CUSTOMER_ENTRY_ID LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON " +
                        "C.CUSTOMER_ENTRY_ID = E.CUSTOMER_ID LEFT JOIN LAM_CUSTOMER_LOAN_REPAYMENT R ON L.LOAN_ID = R.LOAN_ID WHERE L.CUSTOMER_ID = ?";

                loanDetails  =  dbCor.queryForObject(sql_loan, new Object[]{customer.getCustomerID()}, new CustomerLoanDetailsWrapper());
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return loanDetails;
    }

    @Override
    public String ValidateCustomerVerification(String LoanID) throws Exception {
        AesGcmCryptor crypto = new AesGcmCryptor();
        char[] password = {'0', '3', 't', 'u', 'v'};

        String verificationLink = verificationUrl + crypto.encryptString(password, LoanID);

        NotificationService notificationService = new NotificationService();

        notificationService.SendStaffLoanVerificationNotification("tunslike@gmail.com", "09053100351",
                "Finserve Investment Limited", "Babatunde Francis", verificationLink,
                "₦50,000", "6 Months", "2.5%", "₦55,000", "Mrs Olujimi");

        return verificationLink;
    }

    public String FetchCurrentEmployerInterestLoan(String CustomerID) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        String InterestRate = "";

        try {

            //SQL Script
            String sql = "SELECT L.LOAN_INTEREST_RATE FROM LAM_CUSTOMER_EMPLOYERS E LEFT JOIN " +
                    "LAM_COMPANY_LOAN_SETUP L ON E.EMPLOYER_ID = L.COMPANY_PROFILE_ID\n" +
                    "WHERE CUSTOMER_ID = ?";

            InterestRate  =  dbCor.queryForObject(sql, String.class, new Object[] { CustomerID });


        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return InterestRate;
    }

    @Override
    public boolean SubmitCustomerLoanRequest(CustomerLoanRequestDto loanRequest) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_LOAN_REQUEST (LOAN_ID, LOAN_NUMBER, CUSTOMER_ID, LOAN_AMOUNT, LOAN_TENOR, " +
                    "LOAN_PURPOSE, ACCOUNT_DETAILS_ID, MONTHLY_REPAYMENT, TOTAL_REPAYMENT, INTEREST_RATE, " +
                    "FIRST_REPAYMENT_DATE, DATE_CREATED, CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

            String LOAN_ID = UUID.randomUUID().toString();
            Double interestRate = Double.parseDouble(FetchCurrentEmployerInterestLoan(loanRequest.getCustomerID()));
            String LOAN_NUMBER = Utilities.generateUniqueSequenceID(100);
            String firstRepaymentDate = LocalDateTime.now().plusDays(29).toString();

            CalculateLoanSummaryDto loanDetails = new CalculateLoanSummaryDto();
            loanDetails.setCustomerID(loanRequest.getCustomerID());
            loanDetails.setLoanAmount(loanRequest.getLoanAmount());
            loanDetails.setLoanTenor(loanRequest.getLoanTenor());
            loanDetails.setLoanInterest(interestRate.toString());

            CustomerLoanSummary loanSummary = CalculateAmortizeSchedule(loanDetails);

            Double monthlyRepayment = Double.parseDouble(loanSummary.getMonthly_repayment());
            Double totalRepayment = Double.parseDouble(loanSummary.getTotal_repayment());

            //insert
            int status = dbCor.update(sql, LOAN_ID, LOAN_NUMBER, loanRequest.getCustomerID(), loanRequest.getLoanAmount(),
                    loanRequest.getLoanTenor(), loanRequest.getLoanPurpose(), loanRequest.getAccountID(),monthlyRepayment,
                    totalRepayment, interestRate, firstRepaymentDate,
                    LocalDateTime.now(),"SYSTEM");

            if(status == 1) {
                logger.info("Loan request submitted Successfully");

                logger.info("Preparing to send staff verification email");

                return true;
            }else {
                return false;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }


        return false;
    }
}
