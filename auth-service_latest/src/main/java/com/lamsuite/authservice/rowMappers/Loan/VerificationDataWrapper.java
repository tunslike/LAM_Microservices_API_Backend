package com.lamsuite.authservice.rowMappers.Loan;

import com.lamsuite.authservice.model.Loan.VerificationData;
import com.lamsuite.authservice.model.LoanDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificationDataWrapper implements RowMapper<VerificationData> {

    @Override
    public VerificationData mapRow(ResultSet rs, int rowNum) throws SQLException {

        VerificationData verificationData = new VerificationData();

       verificationData.setLAST_NAME(rs.getString("LAST_NAME"));
       verificationData.setFIRST_NAME(rs.getString("FIRST_NAME"));
       verificationData.setPHONE_NUMBER(rs.getString("PHONE_NUMBER"));
       verificationData.setSECTOR(rs.getString("SECTOR"));
       verificationData.setGRADE_LEVEL(rs.getString("GRADE_LEVEL"));
       verificationData.setSERVICE_LENGTH(rs.getString("SERVICE_LENGTH"));
       verificationData.setSTAFF_ID_NUMBER(rs.getString("STAFF_ID_NUMBER"));
       verificationData.setSALARY_PAYMENT_DATE(rs.getString("SALARY_PAYMENT_DATE"));
       verificationData.setANNUAL_SALARY(rs.getString("ANNUAL_SALARY"));
       verificationData.setEMPLOYER_NAME(rs.getString("EMPLOYER_NAME"));
       verificationData.setCONTACT_PERSON(rs.getString("CONTACT_PERSON"));
       verificationData.setCONTACT_EMAIL(rs.getString("CONTACT_EMAIL"));

        return verificationData;
    }
}
