package edu.ithaca.group5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PharmacistTest {


    Client aClient = new Client(2, "djkld", "sdjklfd", "skldjfs", false, 0, "allergy1");
    Pharmacist thePharm = new Pharmacist(5, "sdjkflsd", "fjksldfj", "sldkjfl",0, "");


    @Test
    void createOrderId() {
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00,"allergy1,allergy2,allergy3",false);
        assertEquals(0,theOrder.id, "Constructor doesn't set Id");
    }

    @Test
    void createOrderName(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00,"allergy1,allergy2,allergy3",false);
        assertEquals("A Drug",theOrder.name, "Constructor doesn't set Name");
    }
    @Test
    void createOrderClient(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00,"allergy1,allergy2,allergy3",false);
        assertEquals(aClient,theOrder.client, "Constructor doesn't Assign Client");
    }
    @Test
    void createOrderPrice(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00,"allergy1,allergy2,allergy3",false);
        assertEquals(2.00,theOrder.price, "Constructor doesn't set Price");
    }
    @Test
    void loadOrdersToPPMTest() throws SQLException {
        Order theOrder = thePharm.createOrder(0, "Test", aClient, 2.00, "side effects may inclde...");
        ArrayList<Order> orders = new ArrayList<Order>();
        orders.add(theOrder);
        PPM thePPM = new PPM(true);
        thePharm.loadOrdersToPPM(orders, thePPM);
        thePharm.viewOrders(thePPM);
    }
    @Test
    void resetPassword() {
        thePharm.resetPassword(aClient, "newPass");
        assertTrue(aClient.isPassword("newPass"));
    }
}