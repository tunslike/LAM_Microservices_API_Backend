package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.model.Profile;
import com.lamsuite.authservice.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewProfileRowMapper implements RowMapper<Profile> {

    @Override
    public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {

        Profile profile = new Profile();

        profile.setCustomer_id(rs.getString("CUSTOMER_ENTRY_ID"));
        profile.setUsername(rs.getString("USERNAME"));
        profile.setAccount_type(rs.getString("ACCOUNT_TYPE"));
        profile.setFull_name(rs.getString("FULL_NAME"));
        profile.setPhone_number(rs.getString("PHONE_NUMBER"));
        profile.setEmail_address(rs.getString("EMAIL_ADDRESS"));
        profile.setEmployer_name(rs.getString("COMPANY_NAME"));

        return profile;
    }
}
