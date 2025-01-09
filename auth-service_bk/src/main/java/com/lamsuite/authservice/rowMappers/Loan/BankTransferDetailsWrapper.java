package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.Loan.BankTransferDetails;
import com.lamsuite.authservice.model.LoanDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankTransferDetailsWrapper implements RowMapper<BankTransferDetails> {

    @Override
    public BankTransferDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        BankTransferDetails bankTransferDetails = new BankTransferDetails();

        bankTransferDetails.setBank_id(rs.getString("BANK_ID"));
        bankTransferDetails.setBank_name(rs.getString("BANK_NAME"));
        bankTransferDetails.setAccount_name(rs.getString("ACCOUNT_NAME"));
        bankTransferDetails.setAccount_number(rs.getString("ACCOUNT_NUMBER"));

        return bankTransferDetails;
    }
}
