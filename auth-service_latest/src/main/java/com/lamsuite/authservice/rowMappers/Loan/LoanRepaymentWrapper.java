package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.Loan.BankTransferDetails;
import com.lamsuite.authservice.model.Loan.LoanRepayment;
import com.lamsuite.authservice.model.LoanDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanRepaymentWrapper implements RowMapper<LoanRepayment> {

    @Override
    public LoanRepayment mapRow(ResultSet rs, int rowNum) throws SQLException {

        LoanRepayment repayment = new LoanRepayment();

        repayment.setRepayment_id(rs.getString("REPAYMENT_ID"));
        repayment.setRepayment_amount(rs.getDouble("REPAYMENT_AMOUNT"));
        repayment.setNarration(rs.getString("NARRATION"));
        repayment.setPayment_channel(rs.getString("REPAYMENT_CHANNEL"));
        repayment.setPayment_date(rs.getTimestamp("REPAYMENT_DATE").toLocalDateTime());

        return repayment;
    }
}
