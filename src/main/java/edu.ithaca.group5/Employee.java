package edu.ithaca.group5;

public class Employee extends User {


    public Employee(long id, String name, String username, String password, boolean isFrozen) {
        super(id, name, username, password, isFrozen);
    }

    public Employee(long id, String name, String username, String password) {
        this(id, name, username, password, false);
    }

    /**
     * Takes in an Order object and calls the vadlidated setter to make it true.
     * @param theOrder
     */
    public void validateOrder(Order theOrder){
        theOrder.setValidated(true);
    }
}
