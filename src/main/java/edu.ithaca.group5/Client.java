package edu.ithaca.group5;

import java.util.List;

public class Client extends User {
    List<Order> orders;
    double balance;

    public Client(long id, String name, String username, String password) {
        super(id, name, username, password);
        this.balance = 0;
    }


    public void addFunds(double amount){
        balance += amount;
    }
}
