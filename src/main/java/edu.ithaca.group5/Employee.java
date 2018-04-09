package edu.ithaca.group5;

public class Employee extends User {


    public Employee(long id, String name, String username, String password, boolean isFrozen) {
        super(id, name, username, password, isFrozen);
    }

    public Employee(long id, String name, String username, String password) {
        this(id, name, username, password, false);
    }

    /**
     * Takes in an Order object and calls the vadlidated setter to make it true.
     * @param theOrder
     */
    public void validateOrder(Order theOrder){
        theOrder.setValidated(true);
    }



    /**
     * Allows an employee to remove a customer from the database upon request or unapproved activity
     * @param ppm ppm instance
     * @param client client to remove
     * @return
     */

    public Client removeClient(PPM ppm, Client client){
        ppm.dbConnection.removeClient(client);
        return client;
    }

    public void unfreezeUser(String username, DBConnector connection) {
        User user = connection.getUserByUsername(username);
        unfreezeUser(user, connection);
    }

    public void unfreezeUser(User user, DBConnector connection) {
        user.isFrozen = false;
        connection.unfreezeUser(user);
    }

}
