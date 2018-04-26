package edu.ithaca.group5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

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
        connection.addClient(new Client(1, "test", "user", "pass", true,0, "Allergy1"));
        Employee employee = new Employee(2, "emp", "user2", "pass",false,0);
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
            Client toDelete = new Client(123456, "test1", "un", "pw", false);

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

    @Test
    void viewOrders() {
        try{
            PPM tester = new PPM(true);
            Employee emp = new Employee(1,"empTest","un","pw", false,0, "none");
            emp.viewOrders(tester);

            //this method only prints orders to console and there is currently no way to load orders into the ppm to test


        }catch(java.sql.SQLException e){
            e.printStackTrace();
        }



    }
}

