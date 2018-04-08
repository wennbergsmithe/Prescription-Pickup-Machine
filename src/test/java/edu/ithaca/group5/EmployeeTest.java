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
  
      @Test
    void validateOrder() {
    }

    @Test
    void removeClient() {

        try{
            PPM tester = new PPM(true);
            Client toDelete = new Client(123456,"test1", "un", "pw");

            tester.dbConnection.addClient(toDelete);
            boolean isThere = tester.dbConnection.isInDB(toDelete);

            assertEquals(isThere,true);
            tester.dbConnection.removeClient(toDelete);
            isThere = tester.dbConnection.isInDB(toDelete);
            assertEquals(isThere, false);
        }catch (java.sql.SQLException e){
            e.printStackTrace();
        }

    }
}

