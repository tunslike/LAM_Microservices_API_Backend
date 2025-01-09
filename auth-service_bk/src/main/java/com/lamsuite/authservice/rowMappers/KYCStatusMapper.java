package com.lamsuite.authservice.rowMappers;

import com.lamsuite.authservice.dto.KYCStatus;
import com.lamsuite.authservice.model.CustomerEmployerDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KYCStatusMapper implements RowMapper<KYCStatus> {

    @Override
    public KYCStatus mapRow(ResultSet rs, int rowNum) throws SQLException {

        KYCStatus kycStatus = new KYCStatus();

        kycStatus.setBio_data(rs.getInt("BIO_DATA"));
        kycStatus.setEmp_data(rs.getInt("EMP_DATA"));
        kycStatus.setNok_data(rs.getInt("NOK_DATA"));
        kycStatus.setDoc_data(rs.getInt("DOC_DATA"));

        return kycStatus;
    }


}
