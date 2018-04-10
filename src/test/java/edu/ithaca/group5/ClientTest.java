package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    Client theClient = new Client(0,"John", "johnDoe", "1234", false,"allergy1");

    @Test
    void addAllergy() {
        theClient.addAllergy("allergy2");
        assertEquals(true, theClient.allergies.contains("allergy2"),"Cannot add allergy to list");
    }

    @Test
    void removeAllergy() {
        theClient.allergies = "allergy1,allergy2,allergy3";
        theClient.removeAllergy("allergy1");
        assertEquals(false,theClient.allergies.contains("allergy1"),"Cannot remove allergy successfully");
    }
}