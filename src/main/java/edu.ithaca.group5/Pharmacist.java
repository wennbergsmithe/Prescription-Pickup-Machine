package edu.ithaca.group5;

public class Pharmacist extends Employee {


    public Pharmacist(long id, String name, String username, String password, boolean isFrozen) {
        super(id, name, username, password, isFrozen);
    }

    public Pharmacist(long id, String name, String username, String password) {
        this(id, name, username, password, false);
    }

    public Pharmacist(long id, String name, String username, String password, boolean isFrozen, String salt) {
        super(id, name, username, password, isFrozen, salt);
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
    public Order createOrder(long inId, String inName, Client inClient, double inPrice, String inWarnings){
        Order theOrder = new Order( inId, inName, inClient, inPrice, inWarnings);
        return theOrder;
    }
}

