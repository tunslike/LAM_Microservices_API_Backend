package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.model.Entry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;

public class EntryRowMapper implements RowMapper<Entry> {

    @Override
    public Entry mapRow(ResultSet rs, int rowNum) throws SQLException {

        Entry entry = new Entry();

        entry.setCUSTOMER_ENTRY_ID(rs.getString("CUSTOMER_ENTRY_ID"));
        entry.setUSERNAME(rs.getString("USERNAME"));
        entry.setFULL_NAME(rs.getString("FULL_NAME"));
        entry.setPHONE_NUMBER(rs.getString("PHONE_NUMBER"));
        entry.setEMAIL_ADDRESS(rs.getString("EMAIL_ADDRESS"));
        entry.setEMPLOYER_PROFILE_ID(rs.getString("EMPLOYER_PROFILE_ID"));
        entry.setDATE_CREATED(rs.getTimestamp("DATE_CREATED").toLocalDateTime());
        entry.setIS_LOGGED(rs.getInt("IS_LOGGED"));
        return entry;
    }
}
