package edu.ithaca.group5;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    Client theClient = new Client(0,"John", "johnDoe", "1234", false,0,"allergy2");
    Order theOrder = new Order(0,"Drug A", theClient, 2.99, "allergy1,allergy2,allergy3","1/1/2001" );

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

    @Test
    void allergyFound(){
        theClient.allergies = "allergy1";
        assertEquals(true,theOrder.checkAllergies(),"cannot properly find allergy in first position");
    }

    @Test
    void allergyFound2(){
        assertEquals(true,theOrder.checkAllergies(),"cannot properly find allergy middle");
    }

    @Test
    void allergyFound3(){
        theClient.allergies = "allergy3";
        assertEquals(true,theOrder.checkAllergies(),"cannot find allergy in last position");
    }

    @Test
    void allergyNotFound(){
        theOrder.warnings = "allergy1";
        assertEquals(false,theOrder.checkAllergies(),"finds allergy where there is none");
    }

    @Test
    void refillTest(){
        theOrder.setNextRefill("12/12/2012");
        assertEquals("12/12/2012",theOrder.nextRefill, "Does not set date properly");
    }



}