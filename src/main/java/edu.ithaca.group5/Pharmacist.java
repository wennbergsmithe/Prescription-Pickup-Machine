package edu.ithaca.group5;

public class Pharmacist extends Employee {

    public Order createOrder(long inId, String inName, Client inClient, double inPrice){
        Order theOrder = new Order( inId, inName, inClient, inPrice);
        return theOrder;
    }
}
