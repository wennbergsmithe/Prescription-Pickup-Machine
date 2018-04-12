package edu.ithaca.group5;

import java.util.ArrayList;
import java.util.List;

public class MockConnector implements DBConnector {
    List<User> users;
    List<Order> prescriptions;

    public MockConnector() {
        users = new ArrayList<>();
        prescriptions = new ArrayList<>();
    }

    @Override
    public void addEmployee(Employee employee) {
        users.add(employee);
    }

    @Override
    public void addPharmacist(Pharmacist pharmacist) {
        users.add(pharmacist);
    }

    @Override
    public void addClient(Client client) {
        users.add(client);
    }

    @Override
    public Order addOrder(String inName, String username, double inPrice, String inWarnings) {
        return null;
    }

    @Override
    public Client removeClient(Client clientToRemove){
        users.remove(clientToRemove);
        return clientToRemove;
    }

    public boolean isInDB(Client toCheck){
        return users.contains(toCheck);
    }


    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public int getIDByUsername(String username) {
        int id = 0;
        for (User user : users) {
            if (user.username.equals(username)) {
                return id;
            }
            id++;
        }
        return -1;
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orders = new ArrayList<Order>();

        return orders;
    }

    @Override
    public Order getOrderByNameAndUsername(String orderName, String username) {
        return null;
    }

    @Override
    public void emptyUserTable() {
        users = new ArrayList<>();

    }

    @Override
    public void emptyOrderTable() {
        prescriptions = new ArrayList<>();
    }

    @Override
    public void freezeUser(User user) {
        for (User u : users) {
            if (u.id == user.id) {
                u.isFrozen = true;
            }
        }
    }

    @Override
    public void unfreezeUser(User user) {
        for (User u : users) {
            if (u.id == user.id) {
                u.isFrozen = false;
            }
        }
    }
}
