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

    @Test
    public void createUserTest() throws SQLException {
        //Database Setup
        Statement statement = dbConnection.createStatement();
        ResultSet results;

        String currentName = "";
        String currentUsername = "";
        String currentPassword = "";
        User tempUser;


        //Creating a user without having an active account
        ppm.activeUser = null;
        try {
            currentName = "testAccount1";
            currentUsername = "testAccountUser";
            currentPassword = "testAccountPass1";

            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "client");
            assertNull(tempUser);

        } catch (UsernameTakenException e) {
            System.out.println("Error in createUser testing code: redundant username!");
        }

        //Creating a user who already exists
        //Setup active PPM user
        statement.execute("INSERT INTO user (name, username, password, type) VALUES ('thePharmacist', 'pharmacistmain', 'tempP0', 'pharmacist')");
        ppm.activeUser = new Pharmacist(-1, "thePharmacist", "pharmacistmain", "tempP0");
        //ppm.login("pharmacistmain", "tempP0");

        currentName = "testAccount2";
        currentUsername = "testAccountUser2";
        currentPassword = "testAccountPass2";
        try {
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "pharmacist");
        } catch (UsernameTakenException e) {
            System.out.println("Error in createUser testing code: redundant username!");
        }

        try {
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "pharmacist");
            fail("Failed to throw exception when the desired username was taken");
        }
        catch (UsernameTakenException e){}

        //Different combinations of different types of active users trying to create different types of users
        try {
            //Client as Active User
            statement.execute("INSERT INTO user (name, username, password, type) VALUES ('theClient', 'clientmain', 'tempC0', 'client')");
            ppm.activeUser = new Client(-1, "theClient", "clientmain", "tempC0");
            //CANNOT create a Client
            currentName = "CMadeByC";
            currentUsername = "client1";
            currentPassword = "tempC1";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "client");
            assertNull(tempUser);

            //CANNOT create an Employee
            currentName = "EMadeByC";
            currentUsername = "employee1";
            currentPassword = "tempE1";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "employee");
            assertNull(tempUser);

            //CANNOT create a Pharmacist
            currentName = "PMadeByC";
            currentUsername = "pharmacist1";
            currentPassword = "tempP1";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "pharmacist");
            assertNull(tempUser);



            //Employee as Active User
            statement.execute("INSERT INTO user (name, username, password, type) VALUES ('theEmployee', 'employeemain', 'tempE0', 'employee')");
            ppm.activeUser = new Employee(-1, "theEmployee", "employeemain", "tempE0");
            //CAN create a Client
            currentName = "CMadeByE";
            currentUsername = "client2";
            currentPassword = "tempC2";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "client");
            assertEquals(tempUser.name, currentName);
            assertEquals(tempUser.username, currentUsername);
            assertEquals(tempUser.password, currentPassword);
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    currentUsername + "' and password='" + currentPassword + "'");
            if (results.next()) {
                assertEquals(results.getString("name"), currentName);
                assertEquals(results.getString("username"), currentUsername);
                assertEquals(results.getString("password"), currentPassword);
                assertEquals(results.getString("type"), "client");
            }
            else {
                fail("new user was not added to database");
            }

            //CANNOT create an Employee
            currentName = "EMadeByE";
            currentUsername = "employee2";
            currentPassword = "tempE2";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "employee");
            assertNull(tempUser);

            //CANNOT create a Pharmacist
            currentName = "PMadeByE";
            currentUsername = "pharmacist2";
            currentPassword = "tempP2";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "pharmacist");
            assertNull(tempUser);



            //Pharmacist as Active User
            statement.execute("INSERT INTO user (name, username, password, type) VALUES ('thePharmacist', 'pharmacistmain', 'tempP0', 'pharmacist')");
            ppm.activeUser = new Pharmacist(-1, "thePharmacist", "pharmacistmain", "tempP0");
            //CAN create a Client
            currentName = "CMadeByP";
            currentUsername = "client3";
            currentPassword = "tempC3";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "client");
            assertEquals(tempUser.name, currentName);
            assertEquals(tempUser.username, currentUsername);
            assertEquals(tempUser.password, currentPassword);
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    currentUsername + "' and password='" + currentPassword + "'");
            if (results.next()) {
                assertEquals(results.getString("name"), currentName);
                assertEquals(results.getString("username"), currentUsername);
                assertEquals(results.getString("password"), currentPassword);
                assertEquals(results.getString("type"), "client");
            }
            else {
                fail("new user was not added to database");
            }

            //CAN create an Employee
            currentName = "EMadeByP";
            currentUsername = "employee3";
            currentPassword = "tempE3";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "employee");
            assertEquals(tempUser.name, currentName);
            assertEquals(tempUser.username, currentUsername);
            assertEquals(tempUser.password, currentPassword);
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    currentUsername + "' and password='" + currentPassword + "'");
            if (results.next()) {
                assertEquals(results.getString("name"), currentName);
                assertEquals(results.getString("username"), currentUsername);
                assertEquals(results.getString("password"), currentPassword);
                assertEquals(results.getString("type"), "employee");
            }
            else {
                fail("new user was not added to database");
            }

            //CAN create a Pharmacist
            currentName = "PMadeByP";
            currentUsername = "pharmacist3";
            currentPassword = "tempP3";
            tempUser = ppm.createUser(currentName, currentUsername, currentPassword, "pharmacist");
            assertEquals(tempUser.name, currentName);
            assertEquals(tempUser.username, currentUsername);
            assertEquals(tempUser.password, currentPassword);
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    currentUsername + "' and password='" + currentPassword + "'");
            if (results.next()) {
                assertEquals(results.getString("name"), currentName);
                assertEquals(results.getString("username"), currentUsername);
                assertEquals(results.getString("password"), currentPassword);
                assertEquals(results.getString("type"), "pharmacist");
            }
            else {
                fail("new user was not added to database");
            }

        } catch (UsernameTakenException e) {
            System.out.println("Error in createUser testing code: redundant usernames!");
        }

        statement.close();

    }
}