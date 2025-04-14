package com.lamsuite.authservice.utilities;

import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utilities {

    public static String hashPINSecret(String pinNumber) {
        return BCrypt.hashpw(pinNumber, BCrypt.gensalt());
    }
    public static boolean validatePINNumber(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static String formatCurrencyValue (Double currency) {

        NumberFormat currencyFormatter;
        String currencyOut;
        Locale nigeria = new Locale("en", "NG");
        currencyFormatter = NumberFormat.getCurrencyInstance(nigeria);
        currencyOut = currencyFormatter.format(currency);

        return currencyOut;
    }

    public static String generateUniqueSequenceID (Integer currentCount) {

        String uniqueSequenceID = "";
        DecimalFormat df = new DecimalFormat("0000");

        String pattern = "yyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        uniqueSequenceID = date + df.format(currentCount);

        return uniqueSequenceID;
    }

    public static String generateAccountNumber (Integer currentCount) {

        String uniqueSequenceID = "";
        DecimalFormat df = new DecimalFormat("000");

        String pattern = "yyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        uniqueSequenceID = "1" + date + df.format(currentCount);

        return uniqueSequenceID;
    }

    public static int generateNewPIN() {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(99999));
    }

    public static String generateOTPValue () {
        try {

            Random random = new Random();
            int pin = 10000 + random.nextInt(90000);
            return String.format("%05d", pin);

        }catch (Exception e) {

        }

        return "";
    }

    public static Set<Integer> generateUniquePins(int count) {
        Set<Integer> pinSet = new HashSet<>();
        Random random = new Random();

        while (pinSet.size() < count) {
            int pin = random.nextInt(10000); // Generate a random number between 0 and 9999
            pinSet.add(pin); // Add to set to ensure uniqueness
        }

        return pinSet;
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

    public static HikariDataSource hikariDataSource(String driverClassName, String dbURL,
                                              String dbUsername, String dbPassword) {
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(100);
        ds.setDriverClassName(driverClassName);
        ds.setJdbcUrl(dbURL);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        ds.setConnectionTestQuery("SELECT 1");
        ds.setKeepaliveTime(30000);
        ds.setMaximumPoolSize(20);
        ds.setKeepaliveTime(30000);
        ds.setMaxLifetime(600000);
        ds.setConnectionTimeout(30000);
        ds.addDataSourceProperty("cachePrepStmts" , "true");
        ds.addDataSourceProperty("prepStmtCacheSize" , "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        return ds;
    }
}

/*
* spring.datasource.hikari.mysql.minimum-idle=5
spring.datasource.hikari.mysql.idle-timeout=180000
spring.datasource.hikari.mysql.maximum-pool-size=20
spring.datasource.hikari.mysql.max-lifetime=1800000
spring.datasource.hikari.mysql.connection-timeout=30000
spring.datasource.hikari.mysql.connection-test-query=SELECT 1
spring.datasource.hikari.mysql.validation-timeout=5000
spring.datasource.hikari.pool-name=HikariPool-LamService-MySQL
spring.datasource.hikari.mysql.keep-alive-Time=30000
* */