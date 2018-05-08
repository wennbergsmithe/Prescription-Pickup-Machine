package edu.ithaca.group5;

import java.sql.*;
import java.util.*;

class UsernameTakenException extends Exception {
    String desiredName;
}

class NotAuthorizedException extends Exception {
}

public class PPM {
    DBConnector dbConnection;
    User activeUser;
    final int MAX_LOGIN_ATTEMPTS = 3;
    Map<String, Integer> failedLoginAttempts = new HashMap<>();
    List<Order> loadedOrders = new ArrayList<Order>();
    List<Issue> issues = new ArrayList<Issue>();
    boolean justLoggedIn;
    Robot robot;

    public PPM() throws SQLException {
        setupSQL();
    }

    public PPM(boolean test) throws SQLException {
        if (test) {
            dbConnection = new MockConnector();
        } else {
            setupSQL();
        }
    }

    /**
     * turns on robot
     */
    public void turnOnRobot() {
        if (robot != null) {
            robot.stopValidating();
        }
        robot = new Robot(dbConnection, 5000);
        robot.startValidating();
    }

    /**
     * turns off robot
     */
    public void turnOffRobot() {
        if (robot != null) {
            robot.stopValidating();
            robot = null;
        }
    }

    /**
     * initiates the db connector
     * @throws SQLException
     */
    private void setupSQL() throws SQLException {
        // needed to register db driver
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        dbConnection = new SQLConnector();
    }

    /**
     * Logs user into the PPM. If the user doesn't match anything in the db, returns null, otherwise returns User object
     * @param username User's username
     * @param password User's password
     * @return corresponding User, otherwise null
     */
    public User login(String username, String password) {
        activeUser = dbConnection.getUserByUsernameAndPassword(username, password);
        if (activeUser == null) {
            User match = dbConnection.getUserByUsername(username);
            if (match != null) {
                failedLoginAttempts.put(username, failedLoginAttempts.getOrDefault(username, 0) + 1);
                if (failedLoginAttempts.get(username) >= MAX_LOGIN_ATTEMPTS) {
                    match.isFrozen = true;
                    dbConnection.freezeUser(match);
                }
            }
        }

        if (activeUser != null && activeUser.isFrozen) {
            activeUser = null;
        }

        return activeUser;
    }

    /**
     * Logs the current user out of the system
     * @return the object of the user that just logged out
     */
    public User logout() {
        User pastUser = this.activeUser;
        this.activeUser = null;
        return pastUser;
    }


    /**
     * Determines if someone is currently logged into the ppm
     * @return true if someone is logged in, otherwise false
     */
    public boolean isLoggedIn() {
        if (this.activeUser != null) {
            return true;
        }
        return false;
    }

    /**
     * Creates a user with the provided information. Returns 'null' if unsuccessful (such as if no user is currently
     * logged in or the current user does not have permission to make the desired account type), throws a
     * UsernameTakenException if the desired username is already taken, and returns the created user otherwise
     * @param name User's desired name
     * @param username User's desired username
     * @param password User's desired password
     * @param type User type: can be client, employee, or pharmacist
     * @exception UsernameTakenException thrown if the username is already taken
     * @return the created user, or 'null' if unsuccessful
     */
    public User createUser(String name, String username, String password, String type) throws UsernameTakenException {
        User createdUser = null;

        //Make sure the desired username does not already exist
        User temp = dbConnection.getUserByUsername(username);
        if (temp != null) {
            //Name already exists: throw exception
            UsernameTakenException e = new UsernameTakenException();
            e.desiredName = username;
            throw e;
        }

        //Determine the type of the current active user
        String userType;
        if (activeUser != null) {
            userType = activeUser.getType();
        }
        else {
            userType = "client";
        }

        //Make sure the current active user has permission to create this type of account
        if (userType.equals("client")) {
            //Clients cannot make any accounts
            return createdUser;
        } else if (userType.equals("employee")) {
            //Employees cannot create employees or pharmacists
            if (type.equals("employee")) {
                return createdUser;
            }
            if (type.equals("pharmacist")) {
                return createdUser;
            }
        }

        //User must not already exist: add them to the database
        switch(type) {
            case ("client"):
                Client client = new Client(-1, name, username, password, false);
                dbConnection.addClient(client);
                createdUser = client;
                break;
            case("employee"):
                Employee employee = new Employee(-1, name, username, password, 0);
                dbConnection.addEmployee(employee);
                createdUser = employee;
                break;
            case("pharmacist"):
                Pharmacist pharmacist = new Pharmacist(-1, name, username, password);
                dbConnection.addPharmacist(pharmacist);
                createdUser = pharmacist;
                break;
        }

        if (createdUser != null) {
            //Get the user's database ID
            long id = dbConnection.getIDByUsername(username);
            createdUser.id = id;
        }
        return createdUser;
    }

    /**
     * Loads an order to the ppm's list of loaded orders
     * @param order The order to be loaded
     */
    public void loadOrder(Order order) {
        loadedOrders.add(order);
    }

    /**
     * Gets a list of all the orders currently loaded to the PPM
     * @return The list of orders that are loaded to the PPM
     */
    public List<Order> getLoadedOrders() {
        return loadedOrders;
    }

    /**
     * Adds an issue to the PPM's current list of issues
     * @param name The name of the issue
     * @param description Brief description of the issue
     * @return The created Issue Object, which is null if the description was the empty string
     */
    public Issue addIssue(String name, String description) {
        Issue issueToAdd = null;
        if (!description.equals("")) {
            String username;
            if (this.activeUser == null) {
                username = "";
            } else {
                username = this.activeUser.username;
            }
            issueToAdd = new Issue(name, description, username);
            issues.add(issueToAdd);
        }
        return issueToAdd;
    }

    /**
     * Removes an issue from the PPM's list of current issues
     * @param name The name of the issue to be removed
     * @return The removed issue, or null of the issue was not found in the PPM's list
     */
    public Issue removeIssue(String name) {
        Issue removedIssue = null;

        Iterator<Issue> itr = issues.iterator();
        while (itr.hasNext()) {
            Issue issue = itr.next();
            if (issue.name.toLowerCase().equals(name.toLowerCase())) {
                removedIssue = issue;
                itr.remove();
            }
        }

        return removedIssue;

    }

    /**
     * Labels an issue as solved
     * @param name The name of the issue
     * @return True if the issue was found and is now labeled as solved: false if the issue of the given name was not
     * found in the PPM's list
     */
    public boolean solveIssue(String name) {
        for (Issue issue : issues) {
            if (issue.name.equals(name)) {
                issue.setSolved();
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all issues labeled as solved in the PPM's list of current issues
     */
    public void clearSolvedIssues() {
        List<Issue> solvedIssues = new ArrayList<Issue>();
        for (Issue issue : issues) {
            if (issue.solved) {
                solvedIssues.add(issue);
            }
        }
        for (Issue issue : solvedIssues) {
            issues.remove(issue);
        }
        solvedIssues.clear();
    }

    /**
     * Completely clears the PPM's list of issues
     */
    public void clearIssues() {
        issues.clear();
    }

    /**
     * Change the password of the user argument.
     * @param user the user to change
     * @param newPass the new password
     * @throws NotAuthorizedException if the active user is not a pharmacist.
     */
    public void resetPassword(User user, String newPass) throws NotAuthorizedException {
        if (activeUser.getClass() != Pharmacist.class) {
            throw new NotAuthorizedException();
        }

        ((Pharmacist)activeUser).resetPassword(user, newPass);
        dbConnection.updatePassword(user);
    }

    public boolean returnOrder(String orderName) {
        Order order = dbConnection.getOrderByNameAndUsername(orderName, activeUser.username);
        return returnOrder(order);
    }

    /**
     * Sets the orders paid to false and returns price to the user's balance
     * @param order
     * @return false if the user isn't a client or if the order was not the user's
     */
    public boolean returnOrder(Order order) {
        if(activeUser.getClass() != Client.class) return false;
        if (order.client.id != activeUser.id) return false;

        dbConnection.returnOrder(order);
        ((Client)activeUser).addFunds(order.price);
        dbConnection.updateBalance(activeUser);
        return true;
    }
}
