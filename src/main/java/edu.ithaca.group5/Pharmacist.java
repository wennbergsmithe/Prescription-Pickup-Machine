package edu.ithaca.group5;

public class Pharmacist extends Employee {

    /**
     * Takes in an id, name, client object and price.
     * Creates a new Order object with the Order constructor.
     * @param inId
     * @param inName
     * @param inClient
     * @param inPrice
     * @return theOrder
     */
    public Order createOrder(long inId, String inName, Client inClient, double inPrice){
        Order theOrder = new Order( inId, inName, inClient, inPrice);
        return theOrder;
    }
}