package edu.ithaca.group5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PPMTest {
    private PPM ppm;


    @BeforeAll
    public void setup() throws SQLException {
        ppm = new PPM(true);
        ppm.dbConnection.emptyOrderTable();
        ppm.dbConnection.emptyUserTable();
    }

    @Test
    public void login() throws SQLException {
        // No matching user
        assertNull(ppm.login("test", "test"));

        // Client
        ppm.dbConnection.addClient(new Client(-1, "test", "user", "pass", false,""));
        User user = ppm.login("user", "pass");
        assertEquals("test", user.name);
        assertEquals("user", user.username);
        assertEquals("pass", user.password);
        assertFalse(user.isFrozen);
        assertEquals(Client.class, user.getClass());

        // Employee
        ppm.dbConnection.addEmployee(new Employee(-1, "test2", "user2", "pass2", false, ""));
        user = ppm.login("user2", "pass2");
        assertEquals("test2", user.name);
        assertEquals("user2", user.username);
        assertEquals("pass2", user.password);
        assertFalse(user.isFrozen);
        assertEquals(Employee.class, user.getClass());

        // Pharmacist
        ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test3", "user3", "pass3", false, ""));
        user = ppm.login("user3", "pass3");
        assertEquals("test3", user.name);
        assertEquals("user3", user.username);
        assertEquals("pass3", user.password);
        assertFalse(user.isFrozen);
        assertEquals(Pharmacist.class, user.getClass());

        ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test4", "user4", "pass4", true, ""));
        user = ppm.login("user4", "pass4");
        assertNull(user);

    }

    @Test
    public void maxLoginAttempts() {
        ppm.dbConnection.addEmployee(new Employee(-1, "test", "username", "pass", false, ""));
        for (int i = 0; i < ppm.MAX_LOGIN_ATTEMPTS; i++) {
            User user = ppm.dbConnection.getUserByUsername("username");
            assertFalse(user.isFrozen);
            ppm.login("username", "badpass");
        }
        User user = ppm.dbConnection.getUserByUsername("username");
        assertTrue(user.isFrozen);
    }

}