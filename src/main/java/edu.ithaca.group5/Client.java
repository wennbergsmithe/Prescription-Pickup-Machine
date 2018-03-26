package edu.ithaca.group5;

import java.util.List;

public class Client extends User {
    List<Order> orders;

    public Client(long id, String name, String username, String password) {
        super(id, name, username, password);
    }
}
