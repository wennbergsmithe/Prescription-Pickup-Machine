package edu.ithaca.group5;

public class Employee extends User {

    public void validateOrder(Order theOrder){
        theOrder.setValidated(true);
    }
}
