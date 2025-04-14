package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.model.ResetClient;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetClientMappers implements RowMapper<ResetClient> {

    @Override
    public ResetClient mapRow(ResultSet rs, int rowNum) throws SQLException {

        ResetClient client = new ResetClient();

        client.setFULL_NAME(rs.getString("FULL_NAME"));
        client.setEMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));

        return client;
    }
}
