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
        ppm.dbConnection.addClient(new Client(-1, "test", "user", "pass"));
        User user = ppm.login("user", "pass");
        assertEquals("test", user.name);
        assertEquals("user", user.username);
        assertEquals("pass", user.password);
        assertEquals(Client.class, user.getClass());

        // Employee
        ppm.dbConnection.addEmployee(new Employee(-1, "test2", "user2", "pass2"));
        user = ppm.login("user2", "pass2");
        assertEquals("test2", user.name);
        assertEquals("user2", user.username);
        assertEquals("pass2", user.password);
        assertEquals(Employee.class, user.getClass());

        // Pharmacist
        ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test3", "user3", "pass3"));
        user = ppm.login("user3", "pass3");
        assertEquals("test3", user.name);
        assertEquals("user3", user.username);
        assertEquals("pass3", user.password);
        assertEquals(Pharmacist.class, user.getClass());

    }

}