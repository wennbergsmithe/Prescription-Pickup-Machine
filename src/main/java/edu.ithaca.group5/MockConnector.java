package edu.ithaca.group5;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //Client client = new Client(-1, username, username, "none", false);
        Client client = new Client(-1, inName, username, "none", false);

        Order ordertoadd = new Order(prescriptions.size(), inName, client, inPrice, inWarnings);
        prescriptions.add(ordertoadd);

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


    private static int iterations(int cost)
    {
        if ((cost < 0) || (cost > 30))
            throw new IllegalArgumentException("cost: " + cost);
        return 1 << cost;
    }

    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.isPassword(password)) {
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
        for(Order order : prescriptions){
            if(order.client.username.equals(username)){
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public Order getOrderByNameAndUsername(String orderName, String username){
        for(Order order : prescriptions){
            if(order.client.username.equals(username) && order.name.equals(orderName)){
                return order;
            }
        }
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

    @Override
    public void updatePassword(User user) {
        for (User u : users) {
            if (u.id == user.id) {
                u.passwordSalt = user.passwordSalt;
                u.password = user.password;
            }
        }
    }

    @Override
    public List<Order> getOrders() {return prescriptions;}


}
