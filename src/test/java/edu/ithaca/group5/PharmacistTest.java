package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PharmacistTest {

    Client aClient = new Client();
    Pharmacist thePharm = new Pharmacist();


    @Test
    void createOrderId() {
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00);
        assertEquals(0,theOrder.id, "Constructor doesn't set Id");
    }

    @Test
    void createOrderName(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00);
        assertEquals("A Drug",theOrder.name, "Constructor doesn't set Name");
    }
    @Test
    void createOrderClient(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00);
        assertEquals(aClient,theOrder.client, "Constructor doesn't Assign Client");
    }
    @Test
    void createOrderPrice(){
        Order theOrder  = thePharm.createOrder(0,"A Drug", aClient, 2.00);
        assertEquals(2.00,theOrder.price, "Constructor doesn't set Price");
    }
}