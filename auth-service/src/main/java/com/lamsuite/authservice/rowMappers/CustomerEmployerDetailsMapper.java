package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.model.CustomerEmployerDetails;
import com.lamsuite.authservice.model.EmployerLoanProfile;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerEmployerDetailsMapper implements RowMapper<CustomerEmployerDetails> {


    @Override
    public CustomerEmployerDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        CustomerEmployerDetails employerDetails = new CustomerEmployerDetails();

        employerDetails.setSECTOR(rs.getString("SECTOR"));
        employerDetails.setGRADE_LEVEL(rs.getString("GRADE_LEVEL"));
        employerDetails.setSERVICE_LENGTH(rs.getString("SERVICE_LENGTH"));
        employerDetails.setSTAFF_ID_NUMBER(rs.getString("STAFF_ID_NUMBER"));
        employerDetails.setSALARY_PAYMENT_DATE(rs.getString("SALARY_PAYMENT_DATE"));
        employerDetails.setANNUAL_SALARY(rs.getDouble("ANNUAL_SALARY"));

        return employerDetails;
    }

}
