package com.lamsuite.authservice.services;

import com.lamsuite.authservice.dto.request.*;
import com.lamsuite.authservice.model.Loan.*;
import com.lamsuite.authservice.model.LoanDetails;
import com.lamsuite.authservice.repository.LoanManagement;
import com.lamsuite.authservice.rowMappers.CustomerEmployerDetailsMapper;
import com.lamsuite.authservice.rowMappers.Loan.*;
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
    private static final String dbUsername = "delushsa";
    private static final String dbPassword = "@Dmin123$";

    //LOGGER
    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);
    private static DataSource dataSource;

    @Value("${loanService.verificationURL}")
    private String verificationUrl;

    public LoanManagementService() {
        dataSource = Utilities.hikariDataSource(driverClassName, dbUrl, dbUsername, dbPassword);
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

    public String CalculatePreApprovedAmount(String annualSalary, String loanTenor, String loanInterest, String loanPercentLimit) {

            DecimalFormat df = new DecimalFormat("#.##");

            double annual_salary = Double.parseDouble(annualSalary);
            int loan_tenor = Integer.parseInt(loanTenor);
            double loan_interest = (Double.parseDouble(loanInterest)/100);

            double monthly_salary = annual_salary / 12;

            double max_monthly_salary =  Double.parseDouble(loanPercentLimit) / 100;

            double max_monthly_repayment = max_monthly_salary * monthly_salary;

            double pre_apprv_one = max_monthly_repayment * loan_tenor;

            double pre_apprv_two = 1 + (loan_tenor * loan_interest);

            double preApprovedAmount = pre_apprv_one / pre_apprv_two;

            return df.format(preApprovedAmount);
    }

    @Override
    public SimpleInterestLoanSummary CalculateSimpleInterestLoanCalculation(CalculateLoanSummaryDto loanDetails) throws Exception {
        SimpleInterestLoanSummary loanSummary = new SimpleInterestLoanSummary();
        DecimalFormat df = new DecimalFormat("#.##");

        try {

            loanSummary.setLoanAmount(loanDetails.getLoanAmount());
            loanSummary.setLoanTenor(loanDetails.getLoanTenor());
            loanSummary.setLoanRate(loanDetails.getLoanInterest());

            //calculate monthly principal
            double loanAmount = Double.parseDouble(loanDetails.getLoanAmount());
            int loanTenor = Integer.parseInt( loanDetails.getLoanTenor());
            double monthlyPrincipal = loanAmount / loanTenor;

            loanSummary.setMonthlyPrincipal(df.format(monthlyPrincipal));

            // calculate monthly interest
            double loanRate = Double.parseDouble(loanDetails.getLoanInterest());
            double monthlyInterest = ((double) loanRate / 100) * loanAmount;

            loanSummary.setMonthlyInterest(df.format(monthlyInterest));

            // calculate monthly repayment
            double monthlyRepayment = monthlyPrincipal + monthlyInterest;

            loanSummary.setMonthlyRepayment(df.format(monthlyRepayment));

            //calculate total interest to be paid
            double totalInterestPaid = monthlyInterest * loanTenor;

            loanSummary.setTotalInterestPaid(df.format(totalInterestPaid));

            // calculate total loan repayment
            double totalLoanRepayment = monthlyRepayment * loanTenor;

            loanSummary.setTotalLoanPayment(df.format(totalLoanRepayment));

            double remainingBalance = totalLoanRepayment;

            List<SimpleLoanInterestSchedule> loadBreakdown = new ArrayList<>();

            for (int month = 1; month <= loanTenor; month++) {


                SimpleLoanInterestSchedule schedule = new SimpleLoanInterestSchedule();

                schedule.setPaymentMonth("Month " + month);
                schedule.setLoanBalance(df.format(remainingBalance));
                schedule.setMonthlyRepayment(df.format(monthlyRepayment));
                schedule.setInterestPaid(df.format(monthlyInterest));
                schedule.setPrincipalPaid(df.format(monthlyPrincipal));
                schedule.setNewBalance(df.format(remainingBalance - monthlyRepayment));

                loadBreakdown.add(schedule);

                remainingBalance = remainingBalance - monthlyRepayment;
            }

            loanSummary.setRepaymentSchedule(loadBreakdown);

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

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
                    "L.LOAN_ID = ? GROUP BY " +
                    "L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, L.DATE_CREATED, L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT, L.TOTAL_REPAYMENT, " +
                    "L.INTEREST_RATE, L.LOAN_TENOR, C.EMPLOYER_PROFILE_ID";

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
            String sql = "SELECT COUNT(*)COUNT FROM LAM_CUSTOMER_LOAN_REQUEST WHERE LOAN_STATUS <> 4 AND CUSTOMER_ID = ?";

            Integer count =  dbCor.queryForObject(sql, new Object[] { customer.getCustomerID() }, Integer.class);

            if(count == 1) {

                //SQL Script
                String sql_loan = "SELECT L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, " +
                        "L.DATE_CREATED, L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT, SUM(R.REPAYMENT_AMOUNT)LOAN_TOTAL_REPAYMENT, " +
                        "L.TOTAL_REPAYMENT, L.INTEREST_RATE, L.LOAN_TENOR, (SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE " +
                        "WHERE PROFILE_ID = E.EMPLOYER_ID)EMPLOYER_NAME FROM LAM_CUSTOMER_LOAN_REQUEST L LEFT JOIN " +
                        "LAM_CUSTOMER_ENTRY C ON L.CUSTOMER_ID = C.CUSTOMER_ENTRY_ID LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON " +
                        "C.CUSTOMER_ENTRY_ID = E.CUSTOMER_ID LEFT JOIN LAM_CUSTOMER_LOAN_REPAYMENT R ON L.LOAN_ID = R.LOAN_ID WHERE " +
                        "L.CUSTOMER_ID = ? GROUP BY L.CUSTOMER_ID, L.LOAN_ID, L.LOAN_PURPOSE, L.LOAN_STATUS, L.DATE_CREATED, " +
                        "L.LOAN_NUMBER, L.LOAN_AMOUNT, L.MONTHLY_REPAYMENT,L.TOTAL_REPAYMENT, L.INTEREST_RATE, L.LOAN_TENOR, E.EMPLOYER_ID";

                loanDetails  =  dbCor.queryForObject(sql_loan, new Object[]{customer.getCustomerID()}, new CustomerLoanDetailsWrapper());
            }

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return loanDetails;
    }

    @Override
    public List<LoanRepayment> FetchCustomerLoanRepayment(String LoanID) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        List<LoanRepayment> repayment = new ArrayList<>();

        try {

            //SQL Script
            String sql = "SELECT REPAYMENT_ID, NARRATION, REPAYMENT_AMOUNT, REPAYMENT_DATE, " +
                        "REPAYMENT_CHANNEL FROM LAM_CUSTOMER_LOAN_REPAYMENT " +
                        "WHERE LOAN_ID = ?";

            repayment  =  dbCor.query(sql, new Object[] { LoanID }, new LoanRepaymentWrapper());


        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return repayment;
    }

    @Override
    public List<LoanHistory> FetchLoanHistory(CustomerIDdto customer) {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        List<LoanHistory> loanHistory = new ArrayList<>();

        try {

            //SQL Script
            String sql = "SELECT LOAN_ID, LOAN_NUMBER, LOAN_AMOUNT, AMOUNT_DISBURSED, AUTHORISE_DISBURSE_DATE," +
                    "LOAN_TENOR, LOAN_PURPOSE, LOAN_STATUS FROM LAM_CUSTOMER_LOAN_REQUEST WHERE LOAN_STATUS = 4 AND CUSTOMER_ID = ?";

            loanHistory  =  dbCor.query(sql, new Object[] { customer.getCustomerID() }, new LoanHistoryMapper());


        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return loanHistory;

    }

    private String GenerateLoanVerificationLink(String LoanID) throws Exception {

        AesGcmCryptor crypto = new AesGcmCryptor();
        char[] password = {'0', '3', 't', 'u', 'v'};

        return verificationUrl + crypto.encryptString(password, LoanID);
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

    private String EmailHTMLBody() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                "\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width\">\n" +
                "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "        <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "        <meta name=\"format-detection\" content=\"telephone=no,address=no,email=no,date=no,url=no\">\n" +
                "\n" +
                "        <meta name=\"color-scheme\" content=\"light\">\n" +
                "        <meta name=\"supported-color-schemes\" content=\"light\">\n" +
                "\n" +
                "        \n" +
                "        <!--[if !mso]><!-->\n" +
                "          \n" +
                "          <link rel=\"preload\" as=\"style\" href=\"https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap\">\n" +
                "          <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap\">\n" +
                "\n" +
                "          <style type=\"text/css\">\n" +
                "          // TODO: fix me!\n" +
                "            @import url(https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap);\n" +
                "        </style>\n" +
                "        \n" +
                "        <!--<![endif]-->\n" +
                "\n" +
                "        <!--[if mso]>\n" +
                "          <style>\n" +
                "              // TODO: fix me!\n" +
                "              * {\n" +
                "                  font-family: sans-serif !important;\n" +
                "              }\n" +
                "          </style>\n" +
                "        <![endif]-->\n" +
                "    \n" +
                "        \n" +
                "        <!-- NOTE: the title is processed in the backend during the campaign dispatch -->\n" +
                "        <title></title>\n" +
                "\n" +
                "        <!--[if gte mso 9]>\n" +
                "        <xml>\n" +
                "            <o:OfficeDocumentSettings>\n" +
                "                <o:AllowPNG/>\n" +
                "                <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "            </o:OfficeDocumentSettings>\n" +
                "        </xml>\n" +
                "        <![endif]-->\n" +
                "        \n" +
                "    <style>\n" +
                "        :root {\n" +
                "            color-scheme: light;\n" +
                "            supported-color-schemes: light;\n" +
                "        }\n" +
                "\n" +
                "        html,\n" +
                "        body {\n" +
                "            margin: 0 auto !important;\n" +
                "            padding: 0 !important;\n" +
                "            height: 100% !important;\n" +
                "            width: 100% !important;\n" +
                "\n" +
                "            overflow-wrap: break-word;\n" +
                "            -ms-word-break: break-all;\n" +
                "            -ms-word-break: break-word;\n" +
                "            word-break: break-all;\n" +
                "            word-break: break-word;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        \n" +
                "  direction: undefined;\n" +
                "  center,\n" +
                "  #body_table {\n" +
                "    \n" +
                "  }\n" +
                "\n" +
                "  ul, ol {\n" +
                "    padding: 0;\n" +
                "    margin-top: 0;\n" +
                "    margin-bottom: 0;\n" +
                "  }\n" +
                "\n" +
                "  li {\n" +
                "    margin-bottom: 0;\n" +
                "  }\n" +
                "\n" +
                "  \n" +
                "\n" +
                "  .list-block-list-outside-left li {\n" +
                "    margin-left: 20px !important;\n" +
                "  }\n" +
                "\n" +
                "  .list-block-list-outside-right li {\n" +
                "    margin-right: 20px !important;\n" +
                "  }\n" +
                "\n" +
                "  \n" +
                "    .paragraph {\n" +
                "      font-size: 15px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #5f5f5f;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading1 {\n" +
                "      font-size: 32px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading2 {\n" +
                "      font-size: 26px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading3 {\n" +
                "      font-size: 19px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .list {\n" +
                "      font-size: 15px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #5f5f5f;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "  p a, \n" +
                "  li a {\n" +
                "    \n" +
                "  display: inline-block;  \n" +
                "    color: #5457FF;\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "  .button-table a {\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "  }\n" +
                "\n" +
                "  .paragraph > span {text-decoration: none;}.heading1 > span {text-decoration: none;}.heading2 > span {text-decoration: none;}.heading3 > span {text-decoration: none;}.list > span {text-decoration: none;}\n" +
                "\n" +
                "\n" +
                "        * {\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "        }\n" +
                "\n" +
                "        div[style*=\"margin: 16px 0\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                "\n" +
                "        #MessageViewBody,\n" +
                "        #MessageWebViewDiv {\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            border-collapse: collapse;\n" +
                "            border-spacing: 0;\n" +
                "            mso-table-lspace: 0pt !important;\n" +
                "            mso-table-rspace: 0pt !important;\n" +
                "        }\n" +
                "        table:not(.button-table) {\n" +
                "            border-spacing: 0 !important;\n" +
                "            border-collapse: collapse !important;\n" +
                "            table-layout: fixed !important;\n" +
                "            margin: 0 auto !important;\n" +
                "        }\n" +
                "\n" +
                "        th {\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "\n" +
                "        tr td p {\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                "\n" +
                "        a[x-apple-data-detectors],\n" +
                "\n" +
                "        .unstyle-auto-detected-links a,\n" +
                "        .aBn {\n" +
                "            border-bottom: 0 !important;\n" +
                "            cursor: default !important;\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-family: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        .im {\n" +
                "            color: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        .a6S {\n" +
                "            display: none !important;\n" +
                "            opacity: 0.01 !important;\n" +
                "        }\n" +
                "\n" +
                "        img.g-img+div {\n" +
                "            display: none !important;\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 320px) and (max-device-width: 374px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 320px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 375px) and (max-device-width: 413px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 375px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 414px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 414px !important;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "\n" +
                "    <style>\n" +
                "        @media only screen and (max-device-width: 640px) {\n" +
                "            .contentMainTable {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .single-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .multi-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .imageBlockWrapper {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "        }\n" +
                "        @media only screen and (max-width: 640px) {\n" +
                "            .contentMainTable {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .single-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .multi-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .imageBlockWrapper {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "    <!--[if mso | IE]>\n" +
                "<style>\n" +
                ".button-NbM2kEcc64bf7pieUdilc { padding: 10px 16px; };\n" +
                ".button-NbM2kEcc64bf7pieUdilc a { margin: -10px -16px; }; </style>\n" +
                "<![endif]-->\n" +
                "    \n" +
                "<!--[if mso | IE]>\n" +
                "    <style>\n" +
                "        .list-block-outlook-outside-left {\n" +
                "            margin-left: -18px;\n" +
                "        }\n" +
                "    \n" +
                "        .list-block-outlook-outside-right {\n" +
                "            margin-right: -18px;\n" +
                "        }\n" +
                "\n" +
                "        a:link, span.MsoHyperlink {\n" +
                "            mso-style-priority:99;\n" +
                "            \n" +
                "  display: inline-block;  \n" +
                "    color: #5457FF;\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "\n" +
                "        }\n" +
                "    </style>\n" +
                "<![endif]-->\n" +
                "\n" +
                "\n" +
                "    </head>\n" +
                "\n" +
                "    <body width=\"100%\" style=\"margin: 0; padding: 0 !important; mso-line-height-rule: exactly; background-color: #F5F6F8;\">\n" +
                "        <center role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"width: 100%; background-color: #F5F6F8;\">\n" +
                "            <!--[if mso | IE]>\n" +
                "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" id=\"body_table\" style=\"background-color: #F5F6F8;\">\n" +
                "            <tbody>    \n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                    <![endif]-->\n" +
                "                        <table align=\"center\" role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"640\" style=\"margin: auto;\" class=\"contentMainTable\">\n" +
                "                            <tr class=\"wp-block-editor-spacerblock-v1\"><td style=\"background-color:#F5F6F8;line-height:50px;font-size:50px;width:100%;min-width:100%\">&nbsp;</td></tr><tr class=\"wp-block-editor-imageblock-v1\"><td style=\"background-color:#ffffff;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0\" align=\"center\"><table align=\"center\" width=\"640\" class=\"imageBlockWrapper\" style=\"width:640px\" role=\"presentation\"><tbody><tr><td style=\"padding:0\"><img src=\"https://api.smtprelay.co/userfile/ea69f9c8-69b7-4b87-a1e9-8f67adb98273/finserve_mail_header.png\" width=\"640\" height=\"\" alt=\"\" style=\"border-radius:0px;display:block;height:auto;width:100%;max-width:100%;border:0\" class=\"g-img\"></td></tr></tbody></table></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:14px 32px 32px 32px;background-color:#ffffff\"><p class=\"paragraph\" style=\"font-family:Helvetica, sans-serif;text-align:left;line-height:21.00px;font-size:14px;margin:0;color:#5f5f5f;letter-spacing:0;word-break:normal\"><span style=\"font-weight: bold\" class=\"bold\">Dear {employerContactName},</span><br><br>Trust this mail meets you well. Please be informed that one of your staff has applied for a loan with details below. Kindly verify the staff loan request using the verification button below;<br><br><br><span style=\"font-weight: bold\" class=\"bold\"><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\">Staff Information</span></span><br>Staff Full name:                  <span style=\"font-weight: bold\" class=\"bold\"> </span> <span style=\"font-weight: bold\" class=\"bold\">{staffFullname}</span><br>Staff Phone Number:         <span style=\"font-weight: bold\" class=\"bold\">  {staffPhoneNumber}</span><br>Staff Employer:                    <span style=\"font-weight: bold\" class=\"bold\">{staffEmployer}</span><br><br><span style=\"font-weight: bold\" class=\"bold\"><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\">Staff Loan Details</span></span><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\"><span style=\"font-weight: bold\" class=\"bold\"></span></span><br>Loan Requested Amount:    <span style=\"font-weight: bold\" class=\"bold\">{loanAmount}</span><br>Loan Tenor:                         <span style=\"font-weight: bold\" class=\"bold\">{loanTenor}</span><br>Loan interest:                      <span style=\"font-weight: bold\" class=\"bold\">{loanInterest}</span><br>Total Repayment:                <span style=\"font-weight: bold\" class=\"bold\">{loanRepayment}</span></p></td></tr><tr class=\"wp-block-editor-buttonblock-v1\" align=\"center\"><td style=\"background-color:#ffffff;padding-top:16px;padding-right:20px;padding-bottom:10px;padding-left:20px;width:100%\" valign=\"top\"><table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" class=\"button-table\"><tbody><tr><td valign=\"top\" class=\"button-NbM2kEcc64bf7pieUdilc button-td button-td-primary\" style=\"cursor:pointer;border:none;border-radius:4px;background-color:#5457ff;font-size:16px;font-family:Open Sans, sans-serif;width:fit-content;text-decoration:none;color:#ffffff;overflow:hidden\"><a style=\"color:#ffffff;display:block;padding:10px 16px 10px 16px\" href=\"{verificationLink}\">Verify Staff Loan</a></td></tr></tbody></table></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:20px 20px 20px 20px;background-color:#ffffff\"><p class=\"paragraph\" style=\"font-family:Helvetica, sans-serif;line-height:NaNpx;font-size:14px;margin:0;color:#5f5f5f;letter-spacing:0;word-break:normal\"><br>The Loan Team<br><br><span style=\"font-weight: bold\" class=\"bold\">Finserve Investment Loan Team</span></p></td></tr><tr><td valign=\"top\" align=\"center\" style=\"padding:20px 20px 20px 20px;background-color:#F5F6F8\"><p aria-label=\"Unsubscribe\" class=\"paragraph\" style=\"font-family:Open Sans, sans-serif;text-align:center;line-height:22.00px;font-size:11px;margin:0;color:#5f5f5f;word-break:normal\">If you no longer wish to receive mail from us, you can <a href=\"{unsubscribe}\" data-type=\"mergefield\" data-filename=\"\" data-id=\"c0c4d759-1c22-48d4-a614-785d6acaf420-6V201gHRzhDxAzaNqZiJS\" class=\"c0c4d759-1c22-48d4-a614-785d6acaf420-6V201gHRzhDxAzaNqZiJS\" data-mergefield-value=\"unsubscribe\" data-mergefield-input-value=\"\" style=\"color: #5457FF; display: inline-block;\">unsubscribe</a>.<br>{accountaddress}</p></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:12px 12px 12px 12px;background-color:#F5F6F8\"><p class=\"paragraph\" style=\"font-family:Open Sans, sans-serif;text-align:center;line-height:11.50px;font-size:10px;margin:0;color:#5f5f5f;word-break:normal\">Unable to view? Read it <a href=\"{view}\" data-type=\"mergefield\" data-id=\"62d10d6d-b252-49a7-af10-c771dbd58b15-Xzt0XJLlayJkgPI0XyMI5\" data-filename=\"\" class=\"62d10d6d-b252-49a7-af10-c771dbd58b15-Xzt0XJLlayJkgPI0XyMI5\" data-mergefield-value=\"view\" data-mergefield-input-value=\"\" style=\"color: #5457FF; display: inline-block;\">Online</a></p></td></tr>\n" +
                "                        </table>\n" +
                "                    <!--[if mso | IE]>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </tbody>\n" +
                "            </table>\n" +
                "            <![endif]-->\n" +
                "        </center>\n" +
                "    </body>\n" +
                "</html>";
    }

    @Override
    public boolean SubmitCustomerLoanRequest(CustomerLoanRequestDto loanRequest) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        DecimalFormat df = new DecimalFormat("#.##");

        try {

            //SQL Script
            String sqlcount = "SELECT COUNT(*) FROM LAM_CUSTOMER_LOAN_REQUEST";

            Integer loan_count =  dbCor.queryForObject(sqlcount, Integer.class);

            //SQL Script
            String sql = "INSERT INTO LAM_CUSTOMER_LOAN_REQUEST (LOAN_ID, LOAN_NUMBER, CUSTOMER_ID, LOAN_AMOUNT, LOAN_TENOR, " +
                    "LOAN_PURPOSE, ACCOUNT_DETAILS_ID, MONTHLY_REPAYMENT, TOTAL_REPAYMENT, INTEREST_RATE, " +
                    "FIRST_REPAYMENT_DATE, DATE_CREATED, CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

            Integer newCount = loan_count + 1;
            String LOAN_ID = UUID.randomUUID().toString();
            Double interestRate = Double.parseDouble(FetchCurrentEmployerInterestLoan(loanRequest.getCustomerID()));
            String LOAN_NUMBER = Utilities.generateUniqueSequenceID(newCount);
            String firstRepaymentDate = LocalDateTime.now().plusDays(29).toString();

            CalculateLoanSummaryDto loanDetails = new CalculateLoanSummaryDto();

            loanDetails.setCustomerID(loanRequest.getCustomerID());
            loanDetails.setLoanAmount(loanRequest.getLoanAmount());
            loanDetails.setLoanTenor(loanRequest.getLoanTenor());
            loanDetails.setLoanInterest(interestRate.toString());

            //CustomerLoanSummary loanSummary = CalculateAmortizeSchedule(loanDetails);
            SimpleInterestLoanSummary loanSummary = CalculateSimpleInterestLoanCalculation(loanDetails);

            Double monthlyRepayment = Double.parseDouble(loanSummary.getMonthlyRepayment());
            Double totalRepayment = Double.parseDouble(loanSummary.getTotalLoanPayment());

            //insert
            int status = dbCor.update(sql, LOAN_ID, LOAN_NUMBER, loanRequest.getCustomerID(), loanRequest.getLoanAmount(),
                    loanRequest.getLoanTenor(), loanRequest.getLoanPurpose(), loanRequest.getAccountID(),monthlyRepayment,
                    totalRepayment, interestRate, firstRepaymentDate,
                    LocalDateTime.now(),"SYSTEM");

            if(status == 1) {

                logger.info("Loan request submitted Successfully");

                logger.info("Preparing to send staff verification email");

                // notification service
                NotificationService notificationService = new NotificationService();

                //generate verification link using load ID
                String verificationLink = GenerateLoanVerificationLink(LOAN_ID);

                // get verification data
                VerificationData verificationData = fetchVerificationData(loanRequest.getCustomerID());

                String NewLoanAmount = Utilities.formatCurrencyValue(Double.parseDouble(loanRequest.getLoanAmount()));
                String TotalRepayment = Utilities.formatCurrencyValue(Double.parseDouble((totalRepayment.toString())));
                String MonthlyRepayment = Utilities.formatCurrencyValue(Double.parseDouble((monthlyRepayment.toString())));

                /*
               String EmailAddress, String staffPhoneNumber, String staffEmployer,
                             String staffFullName, String Sector, String GradeLevel, String ServiceLength,
                             String StaffID, String AnnualSalary, String SalaryPaymentDate,
                             String verificationLink,
                             String loanAmount, String loanTenor, String loanInterest,
                             String loanRepayment, String employerContactName
                * */

                notificationService.sendnewemail(
                        verificationData.getCONTACT_EMAIL(),
                        verificationData.getPHONE_NUMBER(),
                        verificationData.getEMPLOYER_NAME(),
                        verificationData.getFIRST_NAME() + " " + verificationData.getLAST_NAME(),
                        verificationData.getSECTOR(),
                        verificationData.getGRADE_LEVEL(),
                        verificationData.getSERVICE_LENGTH(),
                        verificationData.getSTAFF_ID_NUMBER(),
                        verificationData.getANNUAL_SALARY(),
                        verificationData.getSALARY_PAYMENT_DATE(),
                        verificationLink,
                        NewLoanAmount,
                        loanRequest.getLoanTenor(),
                        interestRate + "%",
                        MonthlyRepayment,
                        TotalRepayment,
                        verificationData.getCONTACT_PERSON()
                );
                /*
                //send verification link to company
                notificationService.SendStaffLoanVerificationNotification
                        (
                                verificationData.getCONTACT_EMAIL(),
                                verificationData.getPHONE_NUMBER(),
                                verificationData.getEMPLOYER_NAME(),
                                verificationData.getFIRST_NAME() + " " + verificationData.getLAST_NAME(),
                                verificationLink,
                                NewLoanAmount,
                                loanRequest.getLoanTenor(),
                                interestRate + "%",
                                TotalRepayment,
                                verificationData.getCONTACT_PERSON()
                        );

                 */

                logger.info("Loan verification link has been sent to company!");

                return true;
            }else {
                return false;
            }

        }catch(Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    private VerificationData fetchVerificationData(String CustomerID) throws Exception {

        JdbcTemplate dbCor = new JdbcTemplate(dataSource);

        VerificationData verificationData = new VerificationData();

        try {

            //SQL Script
            String sql = "SELECT LAST_NAME, FIRST_NAME, PHONE_NUMBER, E.SECTOR, E.GRADE_LEVEL, " +
                         "E.SERVICE_LENGTH, E.STAFF_ID_NUMBER, E.ANNUAL_SALARY, E.SALARY_PAYMENT_DATE, " +
                         "(SELECT COMPANY_NAME FROM LAM_COMPANY_PROFILE WHERE PROFILE_ID = E.EMPLOYER_ID)EMPLOYER_NAME, " +
                         "(SELECT CONTACT_PERSON FROM LAM_COMPANY_PROFILE WHERE PROFILE_ID = E.EMPLOYER_ID)CONTACT_PERSON, " +
                         "(SELECT CONTACT_EMAIL FROM LAM_COMPANY_PROFILE WHERE PROFILE_ID = E.EMPLOYER_ID)CONTACT_EMAIL " +
                         "FROM LAM_CUSTOMER C LEFT JOIN LAM_CUSTOMER_EMPLOYERS E ON C.CUSTOMER_ID = E.CUSTOMER_ID WHERE C.CUSTOMER_ID = ?";

            verificationData  =  dbCor.queryForObject(sql, new Object[]{CustomerID}, new VerificationDataWrapper());

        }catch(DataAccessException e) {
            logger.error(e.getMessage());
        }

        return verificationData;
    }
}
