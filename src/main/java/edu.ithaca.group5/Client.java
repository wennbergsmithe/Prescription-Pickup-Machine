package edu.ithaca.group5;

import java.util.List;

public class Client extends User {
    List<Order> orders;

    public Client(long id, String name, String username, String password, boolean isFrozen, String allergies) {
        super(id, name, username, password, isFrozen,allergies);
    }

    public Client(long id, String name, String username, String password, String allergies) {
        this(id, name, username, password, false,allergies);
    }


    public void addFunds(double amount){
        balance += amount;
    }
}
