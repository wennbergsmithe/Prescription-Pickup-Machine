package edu.ithaca.group5;

import java.util.List;

public interface DBConnector {

    /**
     * Adds employee to the database.
     * @param employee
     */
    void addEmployee(Employee employee);

    /**
     * Adds pharamacist to the database.
     * @param pharmacist
     */
    void addPharmacist(Pharmacist pharmacist);

    /**
     * Adds client to the database.
     * @param client
     */
    void addClient(Client client);


    /**
     * removes a client from the database
     * @param clientToRemove
     */
    Client removeClient(Client clientToRemove);


    /**
     * checks to see if the specified client is in the database
     * @param toCheck
     * @return
     */
    boolean isInDB(Client toCheck);


    User getUserByUsernameAndPassword(String username, String password);

    /**
     * Returns the user with the entered username
     * @param username
     * @return
     */
    User getUserByUsername(String username);;

    /**
     * Gets the id of the user with the given username
     * @param username the username of the desired user
     * @return the id of the user with the given username, or -1 if the user was not found
     */
    int getIDByUsername(String username);

    /**
     * empties user table.
     */
    void emptyUserTable();

    /**
     * empties order table.
     */
    void emptyOrderTable();

    void freezeUser(User user);

    void unfreezeUser(User user);


    /**
     * returns an array of orders fron the database
     * @return array of orders
     */
    List<Order> getOrders();

}
