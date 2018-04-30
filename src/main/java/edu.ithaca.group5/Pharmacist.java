package edu.ithaca.group5;

import java.util.ArrayList;

public class Pharmacist extends Employee {



    public Pharmacist(long id, String name, String username, String password, boolean isFrozen, double balance, String salt, String allergies) {
        super(id, name, username, password, isFrozen, salt, balance, allergies);
    }

    public Pharmacist(long id, String name, String username, String password, double balance, String allergies) {
        super(id, name, username, password, false, balance, allergies);
    }

    public Pharmacist(long id, String name, String username, String password) {
        super(id, name, username, password, false, 0, "");
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
    public Order createOrder(long inId, String inName, Client inClient, double inPrice, String inWarnings, boolean easyOpen){
        Order theOrder = new Order( inId, inName, inClient, inPrice, inWarnings, easyOpen);
        return theOrder;
    }
    public void loadOrdersToPPM(ArrayList<Order> orders, PPM thePPM) {
        for (int i = 0; i < orders.size(); i++) {
            thePPM.loadOrder(orders.get(i));
        }
    }
    public void resetPassword(User user, String newPassword) {
        user.setPassword(newPassword);
    }
}

