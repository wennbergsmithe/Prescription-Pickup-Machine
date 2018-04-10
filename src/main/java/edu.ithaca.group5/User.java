package edu.ithaca.group5;

public class User {
    long id;
    String name;
    String username;
    String password; //TODO: make secure
    double balance;
    boolean isFrozen;
    String allergies;

    protected User(long id, String name, String username, String password, boolean isFrozen, String allergies) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.isFrozen = isFrozen;
        this.allergies = allergies;
    }

    public User(long id, String s, String name, String username, boolean isFrozen, String password, String allergies) {
        this(id, name, username, password, false,allergies);
    }
}
