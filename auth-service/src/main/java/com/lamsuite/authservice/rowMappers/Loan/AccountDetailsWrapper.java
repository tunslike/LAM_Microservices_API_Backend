package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.EmployerProfile;
import com.lamsuite.authservice.model.Loan.AccountDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDetailsWrapper implements RowMapper<AccountDetails> {

    @Override
    public AccountDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        AccountDetails accountDetails = new AccountDetails();

        accountDetails.setBANK_ACCOUNT_ID(rs.getString("BANK_ACCOUNT_ID"));
        accountDetails.setBANK_NAME(rs.getString("BANK_NAME"));
        accountDetails.setACCOUNT_NAME(rs.getString("ACCOUNT_NAME"));
        accountDetails.setACCOUNT_NUMBER(rs.getString("ACCOUNT_NUMBER"));
        accountDetails.setDATE_CREATED(rs.getTimestamp("DATE_CREATED").toLocalDateTime());
        accountDetails.setIS_ACTIVE(rs.getInt("IS_ACTIVE"));

        return accountDetails;
    }

}
