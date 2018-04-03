package edu.ithaca.group5;

public class User {
    long id;
    String name;
    String username;
    String password; //TODO: make secure
    double balance;

    protected User(long id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
    }
}
