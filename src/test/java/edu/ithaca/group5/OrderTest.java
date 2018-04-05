package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    Client theClient = new Client(0,"John", "johnDoe", "1234");
    Order theOrder = new Order(0,"Drug A", theClient, 2.99 );

    @Test
    void orderDetails() {
    }

    @Test
    void payOrderNotValidated(){

        theOrder.payOrder("credit",2.99);
        assertEquals(false, theOrder.paid,"Order was set to validated");

    }

    @Test
    void payOrderCredit() {

        theOrder.setValidated(true);
        theOrder.payOrder("credit",2.99);
        assertEquals(true, theOrder.paid,"Order has not been paid with credit");

    }

    @Test
    void payOrderExactCash(){

        theOrder.setValidated(true);
        theOrder.payOrder("cash",2.99);
        assertEquals(true, theOrder.paid,"Order has not been paid with cash");

    }

    @Test
    void payOrderExactBalance(){

        theOrder.setValidated(true);
        theClient.balance = 2.99;
        theOrder.payOrder("balance",2.99);
        assertEquals(true, theOrder.paid,"Order has not been paid");

    }

    @Test
    void payOrderExtraBalance() {

        theOrder.setValidated(true);
        double expected = 3.00 - 2.99;
        theClient.balance = 3.00;
        theOrder.payOrder("balance", 2.99);
        assertEquals(expected, theClient.balance, "Balance was not subtracted properly");
    }

    @Test
    void payOrderNotEnoughBalance(){

        theClient.balance = 3.00;
        theOrder.setPrice(5.00);
        theOrder.payOrder("balance", 2.99);
        assertEquals(false, theOrder.paid, "Balance cannot handle insufficient funds");
    }
}