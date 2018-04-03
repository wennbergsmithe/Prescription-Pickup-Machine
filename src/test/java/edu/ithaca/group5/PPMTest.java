package edu.ithaca.group5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PPMTest {

    public static final String TEST_DB = Config.TEST_DB_HOST;
    public static final String DB_USER = Config.DB_USER;
    public static final String DB_PASSWORD = Config.DB_PASSWORD;
    private Connection dbConnection;
    private PPM ppm;


    @BeforeAll
    public void setup() throws SQLException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        dbConnection = DriverManager.getConnection(TEST_DB, DB_USER, DB_PASSWORD);
        Statement statement = dbConnection.createStatement();
        statement.execute("TRUNCATE TABLE user");
        statement.execute("TRUNCATE TABLE prescription");
        statement.close();
        ppm = new PPM(TEST_DB, DB_USER, DB_PASSWORD);
    }

    @Test
    public void login() throws SQLException {
        // No matching user
        assertNull(ppm.login("test", "test"));

        // Client
        Statement statement = dbConnection.createStatement();
        statement.execute("INSERT INTO user (name, username, password, type) VALUES ('test', 'user', 'pass', 'client')");
        User user = ppm.login("user", "pass");
        assertEquals("test", user.name);
        assertEquals("user", user.username);
        assertEquals("pass", user.password);
        assertEquals(Client.class, user.getClass());

        // Employee
        statement.execute("INSERT INTO user (name, username, password, type) VALUES ('test2', 'user2', 'pass2', 'employee')");
        user = ppm.login("user2", "pass2");
        assertEquals("test2", user.name);
        assertEquals("user2", user.username);
        assertEquals("pass2", user.password);
        assertEquals(Employee.class, user.getClass());

        // Pharmacist
        statement.execute("INSERT INTO user (name, username, password, type) VALUES ('test3', 'user3', 'pass3', 'pharmacist')");
        user = ppm.login("user3", "pass3");
        assertEquals("test3", user.name);
        assertEquals("user3", user.username);
        assertEquals("pass3", user.password);
        assertEquals(Pharmacist.class, user.getClass());

        statement.close();
    }

}