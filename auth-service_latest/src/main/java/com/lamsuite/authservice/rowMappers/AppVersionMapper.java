package com.lamsuite.authservice.rowMappers;


import com.lamsuite.authservice.model.AppVersion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppVersionMapper implements RowMapper<AppVersion> {
    @Override
    public AppVersion mapRow(ResultSet rs, int rowNum) throws SQLException {

        AppVersion appVersion = new AppVersion();

        appVersion.setPlatformId(rs.getString("PLATFORM_ID"));
        appVersion.setPlatform(rs.getString("PLATFORM"));
        appVersion.setLatestVersion(rs.getString("LATEST_VERSION"));
        appVersion.setMinimumVersion(rs.getString("MINIMUM_VERSION"));
        appVersion.setBuildNo(Integer.parseInt(rs.getString("BUILD_NO")));
        appVersion.setDateCreated(rs.getString("DATE_CREATED"));
        appVersion.setDateUpdated(rs.getString("DATE_UPDATED"));
        appVersion.setStatus(rs.getInt("STATUS"));

        return appVersion;
    }
}
