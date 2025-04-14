package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.model.EmployerProfile;
import com.lamsuite.authservice.model.Entry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployerProfileMapper implements RowMapper<EmployerProfile> {
    @Override
    public EmployerProfile mapRow(ResultSet rs, int rowNum) throws SQLException {

        EmployerProfile profile = new EmployerProfile();

        profile.setPROFILE_ID(rs.getString("PROFILE_ID"));
        profile.setCOMPANY_NAME(rs.getString("COMPANY_NAME"));
        profile.setADDRESS(rs.getString("ADDRESS"));
        profile.setAREA_LOCALITY(rs.getString("AREA_LOCALITY"));
        profile.setSTATE(rs.getString("STATE"));
        profile.setCONTACT_PERSON(rs.getString("CONTACT_PERSON"));
        profile.setCONTACT_EMAIL(rs.getString("CONTACT_EMAIL"));
        profile.setDATE_CREATED(rs.getTimestamp("DATE_CREATED").toLocalDateTime());

        return profile;
    }
}
