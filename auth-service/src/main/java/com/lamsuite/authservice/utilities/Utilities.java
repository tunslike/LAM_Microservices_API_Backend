package com.lamsuite.authservice.utilities;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Utilities {

    public static String hashPINSecret(String pinNumber) {
        return BCrypt.hashpw(pinNumber, BCrypt.gensalt());
    }
    public static boolean validatePINNumber(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    public static DriverManagerDataSource initialDataSource(String driverClassName, String dbURL,
                                                            String dbUsername, String dbPassword) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbURL);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
