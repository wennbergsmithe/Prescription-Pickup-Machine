package edu.ithaca.group5;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RobotTest {

    DBConnector dbConnector;
    Robot robot;

    @BeforeAll
    public void setup() {
        dbConnector = new MockConnector();
        dbConnector.emptyOrderTable();
        dbConnector.emptyUserTable();
        robot = new Robot(dbConnector);
    }

    @AfterAll
    public void breakdown() {
        robot.stopValidating();
    }

    @Test
    public void testValidating() throws InterruptedException {
        dbConnector.addClient(new Client(1, "sdjklfsd", "sdjklfsd", "sdjkflds", false));
        dbConnector.addOrder("order1", "sdjklfsd", 6, "sdklfjs", "1/1/2000");
        dbConnector.addOrder("order2", "sdjklfsd", 6, "sdklfjs", "1/1/2000");
        dbConnector.addOrder("order3", "sdjklfsd", 6, "sdklfjs", "1/1/2000");
        for (Order order : dbConnector.getOrders()) {
            assertFalse(order.isValidated);
        }


        robot.startValidating();
        Thread.sleep(6000); //sleep longer than Robot sleeps
        for (Order order : dbConnector.getOrders()) {
            assertTrue(order.isValidated);
        }

        dbConnector.addOrder("order5", "sdjklfsd", 6, "sdklfjs", "1/1/2000");
        Thread.sleep(6000); //sleep longer than Robot sleeps
        for (Order order : dbConnector.getOrders()) {
            assertTrue(order.isValidated);
        }

        robot.stopValidating();
        dbConnector.addOrder("order6", "sdjklfsd", 6, "sdklfjs", "1/1/2000");
        Thread.sleep(6000);
        assertFalse(dbConnector.getOrderByNameAndUsername("order6", "sdjklfsd").isValidated);

        robot.startValidating();
        Thread.sleep(6000); //sleep longer than Robot sleeps
        for (Order order : dbConnector.getOrders()) {
            assertTrue(order.isValidated);
        }


    }

}