package edu.ithaca.group5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest {

    DBConnector connection;

    @BeforeEach
    public void getDB() {
        connection = new MockConnector();
        connection.emptyUserTable();
        connection.emptyOrderTable();
    }

    @Test
    public void unfreezeTest() {
        connection.addClient(new Client(1, "test", "user", "pass", true));
        Employee employee = new Employee(2, "emp", "user2", "pass");
        User user = connection.getUserByUsername("user");
        assertTrue(user.isFrozen);
        employee.unfreezeUser("user", connection);
        user = connection.getUserByUsername("user");
        assertFalse(user.isFrozen);
    }
}
