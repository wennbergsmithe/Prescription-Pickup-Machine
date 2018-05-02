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
     * Adds the given order to the database, then returns it.
     * @param inName Name of the order
     * @param username Username of the client who will be given the order
     * @param inPrice Price of the order
     * @param inWarnings List of allergy warnings for the order
     * @param easyOpen boolean if the prescription needs to have easy open caps
     * @return the created order, or null if there was an error
     */
  
  
    Order addOrder(String inName, String username, double inPrice, String inWarnings, String inRefillDate boolean easyOpen);

    /**
     * removes a client from the database
     * @param clientToRemove
     */
    Client removeClient(Client clientToRemove);

    /**
     * Sets paid to false in db.
     * @return true if the order exists and had already been paid for. false otherwise
     */
    boolean returnOrder(Order order);

    void setPaidTrue(Order order);


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
    User getUserByUsername(String username);

    /**
     * Gets the id of the user with the given username
     * @param username the username of the desired user
     * @return the id of the user with the given username, or -1 if the user was not found
     */
    int getIDByUsername(String username);

    /**
     * Returns a list of all of the orders belonging to the user of the given username
     * @param username The username of the specified user
     * @return List of all the orders belonging to the given user: will be an empty list if there is an error
     */
    List<Order> getOrdersByUsername(String username);

    /**
     * Gets the order of the desired name that belongs to the user with the given username
     * @param orderName The name of the order to be retrieved
     * @param username The username of the user which the order belongs to
     * @return The order of the desired name belonging to the desired user, or null if the order was not found
     */
    Order getOrderByNameAndUsername(String orderName, String username);

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

    void updatePassword(User user);


    void updateEasyOpen(Order order, boolean newBool);

    void updateBalance(User user);

    void validateAllOrders();



    /**
     * returns an array of orders fron the database
     * @return array of orders
     */
    List<Order> getOrders();

}
