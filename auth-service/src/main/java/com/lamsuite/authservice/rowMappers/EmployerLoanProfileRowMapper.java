package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.model.EmployerLoanProfile;
import com.lamsuite.authservice.model.EmployerProfile;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployerLoanProfileRowMapper implements RowMapper<EmployerLoanProfile> {
    @Override
    public EmployerLoanProfile mapRow(ResultSet rs, int rowNum) throws SQLException {

        EmployerLoanProfile empProfile = new EmployerLoanProfile();

        empProfile.setCOMPANY_PROFILE_ID(rs.getString("COMPANY_PROFILE_ID"));
        empProfile.setNO_OF_STAFF(rs.getInt("NO_OF_STAFF"));
        empProfile.setLOAN_LIMIT_PERCENT(rs.getDouble("LOAN_LIMIT_PERCENT"));
        empProfile.setLOAN_INTEREST_RATE(rs.getDouble("LOAN_INTEREST_RATE"));
        empProfile.setLOAN_TENOR(rs.getInt("LOAN_TENOR"));
        empProfile.setPAYMENT_STRUCTURE(rs.getString("REPAYMENT_STRUCTURE"));
        empProfile.setDATE_CREATED(rs.getTimestamp("DATE_CREATED").toLocalDateTime());

        return empProfile;
    }
}
