package edu.ithaca.group5;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Pharmacist extends Employee {


    public Pharmacist(long id, String name, String username, String password) {
        super(id, name, username, password);
    }



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

