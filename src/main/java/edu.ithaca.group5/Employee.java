package edu.ithaca.group5;

public class Employee extends User {

    /**
     * Takes in an Order object and calls the vadlidated setter to make it true.
     * @param theOrder
     */
    public void validateOrder(Order theOrder){
        theOrder.setValidated(true);
    }
}
