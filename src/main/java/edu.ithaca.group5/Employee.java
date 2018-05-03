package edu.ithaca.group5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Employee extends User {

    public Employee(long id, String name, String username, String password, boolean isFrozen, double balance) {
        super(id, name, username, password, isFrozen, balance, "");
    }

    public Employee(long id, String name, String username, String password, double balance) {
        this(id, name, username, password, false, balance);
    }

    public Employee(long id, String name, String username, String password, boolean isFrozen, String salt, double balance, String allergies) {
        super(id, name, username, password, isFrozen, salt, balance, allergies);
    }

    public Employee(long id, String name, String username, String password, boolean isFrozen, double balance, String allergies) {
        super(id, name, username, password, isFrozen, balance, allergies);
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

    /**
     * ufreezes user
     * @param username
     * @param connection
     */
    public void unfreezeUser(String username, DBConnector connection) {
        User user = connection.getUserByUsername(username);
        unfreezeUser(user, connection);
    }

    /**
     * unfreezes user
     * @param user
     * @param connection
     */
    public void unfreezeUser(User user, DBConnector connection) {
        user.isFrozen = false;
        connection.unfreezeUser(user);
    }



    /**
     * Prints out a list of current orders in the PPM database
     */

    public void viewOrders(PPM ppm){
        List<Order> list = ppm.dbConnection.getOrders();

        Iterator<Order> itr = list.iterator();

        while (itr.hasNext()){
            System.out.println(itr.next());
            System.out.println("\n\n");
        }
    }

    /*
     *  Sends a message to MD
     */

    public void sendMDNotice(Client user, String prescriptionName, int daysBeforeRunningOut){
        System.out.println("Sent : " + user.username + "Prescription: " + prescriptionName + "Expires in: " + daysBeforeRunningOut);
    }
}
