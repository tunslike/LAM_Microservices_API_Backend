package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.Loan.AccountDetails;
import com.lamsuite.authservice.model.Loan.LoanHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanHistoryMapper implements RowMapper<LoanHistory> {

    @Override
    public LoanHistory mapRow(ResultSet rs, int rowNum) throws SQLException {

        LoanHistory loanHistory = new LoanHistory();

        loanHistory.setLoan_id(rs.getString("LOAN_ID"));
        loanHistory.setLoan_number(rs.getString("LOAN_NUMBER"));
        loanHistory.setLoan_amount(rs.getDouble("AMOUNT_DISBURSED"));
        loanHistory.setLoan_tenor(rs.getString("LOAN_TENOR"));
        loanHistory.setLoan_purpose(rs.getString("LOAN_PURPOSE"));
        loanHistory.setLoan_status(rs.getString("LOAN_STATUS"));
        loanHistory.setLoan_date(rs.getTimestamp("AUTHORISE_DISBURSE_DATE").toLocalDateTime());

        return loanHistory;
    }
}
