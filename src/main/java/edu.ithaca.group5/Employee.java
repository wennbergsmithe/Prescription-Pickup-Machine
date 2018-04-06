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

    public void unfreezeUser(String username, DBConnector connection) {
        User user = connection.getUserByUsername(username);
        unfreezeUser(user, connection);
    }

    public void unfreezeUser(User user, DBConnector connection) {
        user.isFrozen = false;
        connection.unfreezeUser(user);
    }
}
