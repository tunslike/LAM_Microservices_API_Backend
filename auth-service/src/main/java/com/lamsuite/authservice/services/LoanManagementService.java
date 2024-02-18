package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.request.CustomerAccountDetailsDto;
import com.lamsuite.authservice.dto.request.CustomerLoanRequestDto;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.repository.LoanManagement;
import com.lamsuite.authservice.rowMappers.Loan.AccountDetailsWrapper;
import com.lamsuite.authservice.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
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

    public LoanManagementService() {
        dataSource = Utilities.initialDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
    }

    @Override
    public boolean AddCustomerAccountDetails(CustomerAccountDetailsDto accountDetails) throws Exception {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

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
    public List<AccountDetails> FetchCustomerAccountDetails(String CustomerID) {
        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        List<AccountDetails> accountDetails = null;

        try {

            //SQL Script
            String sql = "SELECT BANK_ACCOUNT_ID, BANK_NAME, ACCOUNT_NUMBER, ACCOUNT_NAME, DATE_CREATED, IS_ACTIVE FROM LAM_CUSTOMER_BANK_ACCOUNTS WHERE CUSTOMER_ID = 'f1f694e7-6f21-466d-a108-6a1b63cb8061'";

            accountDetails = dbCor.query(sql, new AccountDetailsWrapper());

            return accountDetails;

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean SubmitCustomerLoanRequest(CustomerLoanRequestDto loanRequest) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        try {

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_LOAN_REQUEST (LOAN_ID, CUSTOMER_ID, LOAN_AMOUNT, LOAN_TENOR, " +
                    "LOAN_PURPOSE, ACCOUNT_DETAILS_ID, MONTHLY_REPAYMENT, TOTAL_REPAYMENT, INTEREST_RATE, " +
                    "FIRST_REPAYMENT_DATE, DATE_CREATED, CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            String LOAN_ID = UUID.randomUUID().toString();
            Double monthlyRepayment = 35000.00;
            Double totalRepayment = 180390.00;
            Double interestRate = 2.5;
            String firstRepaymentDate = LocalDateTime.now().toString();

            //insert
            int status = dbCor.update(sql, LOAN_ID, loanRequest.getCustomerID(), loanRequest.getLoanAmount(),
                    loanRequest.getLoanTenor(), loanRequest.getLoanPurpose(), loanRequest.getAccountID(),monthlyRepayment,
                    totalRepayment, interestRate, firstRepaymentDate,
                    LocalDateTime.now(),"SYSTEM");

            if(status == 1) {
                logger.info("Loan request submitted Successfully");
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
