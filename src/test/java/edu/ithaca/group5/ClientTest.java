package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    Client theClient = new Client(0,"John", "johnDoe", "1234", false,0,"");

    @Test
    void addAllergy() {
        theClient.addAllergy("allergy2");
        assertEquals(true, theClient.allergies.contains("allergy2"),"Cannot add allergy to list");
    }

    @Test
    void removeAllergy() {
        theClient.allergies = "allergy1,allergy2,allergy3";
        theClient.removeAllergy("allergy1");
        assertEquals(false,theClient.allergies.contains("allergy1"),"Cannot remove allergy from beginning");
    }

    @Test
    void removeAllergy2(){
        theClient.allergies = "allergy1,allergy2,allergy3";
        theClient.removeAllergy("allergy2");
        assertEquals(false, theClient.allergies.contains("allergy2"), "Cannot remove allergy from middle");
    }

    @Test
    void removeAllergy3(){
        theClient.allergies = "allergy1,allergy2,allergy3";
        theClient.removeAllergy("allergy3");
        assertEquals(false, theClient.allergies.contains("allergy3"), "Cannot remove allergy from end");
    }
    @Test
    void discontinueOrderTest() {
        Order theOrder = new Order(0, "Order1", theClient, 1, "generic order has side effects", "3");
        theClient.orders.add(theOrder);
        boolean isOrderRemoved = theClient.discontinueOrder("Order1");
        assertEquals(true, isOrderRemoved, "Error removing order");
    }
    @Test
    void requestManufacturerTest(){
        String requestedManufacturer = theClient.requestManufacturer("DrugCo");
        assertEquals("DrugCo", requestedManufacturer);
    }
}