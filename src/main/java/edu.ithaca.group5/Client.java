package edu.ithaca.group5;

import java.util.List;

public class Client extends User {
    List<Order> orders;

    public Client(long id, String name, String username, String password, boolean isFrozen) {
        super(id, name, username, password, isFrozen);
    }

    public Client(long id, String name, String username, String password) {
        this(id, name, username, password, false);
    }


    public void addFunds(double amount){
        balance += amount;
    }
}
