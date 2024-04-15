package com.lamsuite.authservice.utilities;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Utilities {

    public static String hashPINSecret(String pinNumber) {
        return BCrypt.hashpw(pinNumber, BCrypt.gensalt());
    }
    public static boolean validatePINNumber(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static String generateUniqueSequenceID (int currentCount) {
        String uniqueSequenceID = "";
        DecimalFormat df = new DecimalFormat("00000");

        String pattern = "yyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        uniqueSequenceID = date + df.format(currentCount);

        return uniqueSequenceID;
    }

    public static long generateCustomerNumber() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextLong(10_000_000_000L, 100_000_000_000L);
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
