package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.LoanDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerLoanDetailsWrapper implements RowMapper<LoanDetails> {

    @Override
    public LoanDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        LoanDetails loanDetails = new LoanDetails();

        loanDetails.setCUSTOMER_ID(rs.getString("CUSTOMER_ID"));
        loanDetails.setLOAN_ID(rs.getString("LOAN_ID"));
        loanDetails.setLOAN_PURPOSE(rs.getString("LOAN_PURPOSE"));
        loanDetails.setLOAN_STATUS(rs.getInt("LOAN_STATUS"));
        loanDetails.setLOAN_NUMBER(rs.getString("LOAN_NUMBER"));
        loanDetails.setLOAN_AMOUNT(rs.getDouble("LOAN_AMOUNT"));
        loanDetails.setMONTHLY_REPAYMENT(rs.getDouble("MONTHLY_REPAYMENT"));
        loanDetails.setTOTAL_REPAYMENT(rs.getDouble("TOTAL_REPAYMENT"));
        loanDetails.setLOAN_TOTAL_REPAYMENT(rs.getDouble("LOAN_TOTAL_REPAYMENT"));
        loanDetails.setINTEREST_RATE(rs.getDouble("INTEREST_RATE"));
        loanDetails.setLOAN_TENOR(rs.getInt("LOAN_TENOR"));
        loanDetails.setEMPLOYER_NAME(rs.getString("EMPLOYER_NAME"));
        loanDetails.setAUTHORISE_DISBURSE_DATE(rs.getTimestamp("DATE_CREATED").toLocalDateTime());

        return loanDetails;
    }

}
