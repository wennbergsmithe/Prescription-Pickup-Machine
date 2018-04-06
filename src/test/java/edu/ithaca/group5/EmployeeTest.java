package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void validateOrder() {
    }

    @Test
    void removeClient() {

        try{
            PPM tester = new PPM(true);
            Client toDelete = new Client(123456,"Eli Wennberg Smith", "wennbergsmithe", "password");

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