package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDetailsRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setTransaction_id(rs.getString("TRANSACTION_ID"));
        transaction.setCustomer_id(rs.getString("CUSTOMER_ID"));
        transaction.setAmount(rs.getDouble("TRANSACTION_AMOUNT"));
        transaction.setTransaction_type(rs.getString("TRANSACTION_TYPE"));
        transaction.setNarration(rs.getString("NARRATION"));
        transaction.setDate_created(rs.getTimestamp("DATE_CREATED").toLocalDateTime());
        transaction.setCreated_by(rs.getString("CREATED_BY"));

        return transaction;
    }

}
