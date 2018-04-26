package edu.ithaca.group5;

import java.sql.*;
import java.util.*;

class UsernameTakenException extends Exception {
    String desiredName;
}

public class PPM {
    DBConnector dbConnection;
    User activeUser;
    final int MAX_LOGIN_ATTEMPTS = 3;
    Map<String, Integer> failedLoginAttempts = new HashMap<>();
    List<Order> loadedOrders = new ArrayList<Order>();
    List<Issue> issues = new ArrayList<Issue>();
    boolean justLoggedIn;

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
        for (Issue issue : issues) {
            if (issue.solved) {
                issues.remove(issue);
            }
        }
    }

    /**
     * Completely clears the PPM's list of issues
     */
    public void clearIssues() {
        issues.clear();
    }

    private static void printBreak() {
        System.out.println("----------------------------------------------------------");
    }

    private static void printCommands() {
        printBreak();
        System.out.println("Current Commands:");
        printBreak();
        System.out.println("General Commands:\n");
        System.out.println("help - Displays this list of commands");
        System.out.println("login - Log into an existing account");
        System.out.println("logout - Log out of the current account");
        System.out.println("exit - Ends the program");
        System.out.println("listloadedorders - Lists the orders that are currently in the PPM");
        System.out.println("report - Report an issue with the PPM");
        printBreak();
        System.out.println("Client Commands:\n");
        System.out.println("pay - Pay for an order");
        System.out.println("listorders - List your current orders");
        System.out.println("addfunds - Add funds to your account");
        printBreak();
        System.out.println("Pharmacist/Employee Commands:");
        System.out.println("Keep in mind Employees can only operate on client accounts\n");
        System.out.println("create - Creates a new account");
        System.out.println("addorder - Add a new order to a client's account");
        System.out.println("validate - Validate an order");
        System.out.println("load - Load an order to the PPM");
        System.out.println("remove - Delete a user from the PPM's database");
        System.out.println("unblock - Disable the lock on an account");
        System.out.println("issues - Lists all current issues with the PPM and their solved/unsolved status");
        System.out.println("solveissue - Label an issue as solved");
        System.out.println("removeissue - Delete a specified issue");
        System.out.println("clearsolvedissues - Delete all solved issues from the PPM");
        System.out.println("clearissues - Delete all current issues with the PPM, even if they are not solved");

    }

    public static void main(String[] args) {
        try {
            PPM ppm = new PPM(true);
            ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test pharmacist", "testPharmacist", "password"));
            ppm.login("testPharmacist", "password");
            Scanner console = new Scanner(System.in);

            //Print welcome message
            System.out.println("Welcome to the Prescription Pickup Machine!");
            System.out.println("Type 'help' for a list of commands");

            String currentInput;
            boolean done = false;
            while (!done) {
                printBreak();
                //Print status screen
                if (ppm.activeUser != null) {
                    System.out.println("\nCurrently logged in as " + ppm.activeUser.name);
                    if (ppm.activeUser.getType().equals("client")) {
                        System.out.println("Your account balance: $" + ppm.activeUser.balance);
                    } else {
                        System.out.println("You have administrative privileges");
                    }
                    System.out.println();
                } else {
                    System.out.println("\nNobody is currently logged in\n");
                }

                //Print message for a new user logging in
                if (ppm.justLoggedIn) {
                    //Print any new issues with the PPM (only if the account is an employee)
                    if (ppm.activeUser != null && !ppm.activeUser.getType().equals("client")) {
                        String issues = "";
                        for (Issue issue : ppm.issues) {
                            if (issue.isNew) {
                                issues += issue.toString() + '\n';
                                issue.setOld();
                            }
                        }
                        if (!issues.equals("")) {
                            System.out.println("There were new issues reported within the PPM that need your attention!");
                            System.out.println(issues);
                        }
                    }

                    ppm.justLoggedIn = false;
                }

                currentInput = console.nextLine();
                if (currentInput.equals("create")) {
                    System.out.println("To create an account you must provide a name, username, password, and type");
                    System.out.println("Enter a name:");
                    String name = console.nextLine();
                    System.out.println("Enter a username:");
                    String username = console.nextLine();
                    System.out.println("Enter a password:");
                    String password = console.nextLine();
                    System.out.println("Enter an account type (client, employee, or pharmacist):");
                    String type = console.nextLine();
                    type.toLowerCase();

                    //Make sure the type is an elligible type
                    if (type.equals("client") || type.equals("employee") || type.equals("pharmacist")) {
                        try {
                            User createdUser = ppm.createUser(name, username, password, type);
                            if (createdUser == null) {
                                System.out.println("Error: you don't have permission to create that type of user!");
                            } else {
                                System.out.println("Successfully created a new user with a name of '" + createdUser.name + "'!");
                            }

                        } catch (UsernameTakenException e) {
                            System.out.println("Error: the username '" + e.desiredName + "' is already taken");
                        }
                    } else {
                        System.out.println("Error: not an eligible account type");
                    }
                    //Add another line for neatness
                    System.out.println();
                } else if (currentInput.equals("remove")) {
                    if (ppm.activeUser != null) {
                        if (ppm.activeUser.getType().equals("pharmacist")) {
                            System.out.println("Enter the username of the user you want to delete:");
                            String username = console.nextLine();

                            User user = ppm.dbConnection.getUserByUsername(username);
                            if (user != null) {
                                Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);
                                System.out.println("Are you sure? (Y/N)");
                                String prompt = null;
                                while (prompt == null) {
                                    prompt = console.nextLine();
                                    if (prompt.toLowerCase().equals("y")) {
                                        System.out.println("Deleting " + username + "'s account...");
                                        ppm.dbConnection.removeClient(client);
                                        System.out.println("Successfully deleted " + username + "'s account");
                                    } else if (prompt.toLowerCase().equals("n")) {
                                        prompt = "escape";
                                    } else {
                                        System.out.println("Bad input: try again");
                                        prompt = null;
                                    }
                                }
                            } else {
                                System.out.println("No user exists with a username of " + username);
                            }
                        } else {
                            System.out.println("You do not have permission to delete users!");
                        }
                    } else {
                        System.out.println("Please log in before attempting to delete a user");
                    }

                } else if (currentInput.equals("login")) {
                    if (!ppm.isLoggedIn()) {
                        System.out.println("Enter a username:");
                        String username = console.nextLine();
                        System.out.println("Enter a password:");
                        String password = console.nextLine();

                        User newUser = ppm.login(username, password);
                        if (newUser != null) {
                            System.out.println("Successfully logged into " + newUser.name + "'s account!");
                            ppm.activeUser = newUser;
                            ppm.justLoggedIn = true;
                        } else {
                            User possibleUser = ppm.dbConnection.getUserByUsername(username);
                            if (possibleUser != null && possibleUser.isFrozen) {
                                System.out.println("This account has been locked due to excessive login attempts." +
                                        " Contact your local employee for assistance.");
                            } else {
                                System.out.println("Error: could not complete the login");
                            }
                        }
                        //Add another line for neatness
                        System.out.println();
                    } else {
                        System.out.println("Log out of the current account before logging in!");
                    }
                } else if (currentInput.equals("unblock")) {
                    if (ppm.activeUser != null && !ppm.activeUser.getType().equals("client")) {
                        System.out.println("Enter the username of the account you want to unblock");
                        String username = console.nextLine();
                        User blockedUser = ppm.dbConnection.getUserByUsername(username);
                        if (blockedUser.isFrozen) {
                            ppm.dbConnection.unfreezeUser(blockedUser);
                            System.out.println(username + "'s account has been unblocked.");
                        } else {
                            System.out.println("That user's account is not blocked!");
                        }
                    } else {
                        System.out.println("You do not have permission to unblock accounts!");
                    }
                } else if (currentInput.equals("logout")) {
                    User pastUser = ppm.logout();
                    if (pastUser != null) {
                        System.out.println(pastUser.name + " has been logged out.");
                    } else {
                        System.out.println("Cannot log someone out if nobody is logged in!");
                    }
                } else if (currentInput.equals("exit")) {
                    System.out.println("Exiting...");
                    done = true;
                } else if (currentInput.equals("addorder")) {
                    System.out.println("Enter the username of the client who will receive the order:");
                    String username = console.nextLine();
                    User user = ppm.dbConnection.getUserByUsername(username);
                    if (user != null) {
                        if (user.getType().equals("client")) {
                            Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);
                            System.out.println("What is the name of the order?");
                            String inName = console.nextLine();
                            System.out.println("What is the price of this order?");
                            String inPrice = console.nextLine();
                            System.out.println("Enter any warnings for this order, or 'none' if there are none:");
                            String inWarnings = console.nextLine();
                            if (inWarnings.toLowerCase().equals("none")) {
                                inWarnings = "";
                            }

                            Order tempOrder = new Order(-1, "", client, 0, inWarnings);
                            if (tempOrder.checkAllergies()) {
                                System.out.println("There's an allergy confliction with this medication!\n" +
                                        "Do you still want to give this order to the client? (Y/N)");
                                String prompt = null;
                                while (prompt == null) {
                                    prompt = console.nextLine();
                                    if (prompt.toLowerCase().equals("y")) {
                                        Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings);
                                        client.orders.add(order);
                                        System.out.println("Successfully gave the order to the client");
                                    } else if (prompt.toLowerCase().equals("n")) {
                                        prompt = "escape";
                                    } else {
                                        System.out.println("Bad input: try again");
                                        prompt = null;
                                    }
                                }
                            } else {
                                Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings);
                                client.orders.add(order);
                                System.out.println("Successfully gave the order to the client");
                            }
                        } else {
                            System.out.println("That user is not a client!");
                        }
                    } else {
                        System.out.println("No user exists with username " + username);
                    }
                    System.out.println("");
                } else if (currentInput.equals("validate")) {
                    if (ppm.activeUser != null) {
                        if (ppm.activeUser.getType().equals("pharmacist")) {
                            System.out.println("What is the username of the client that the order is for?");
                            String username = console.nextLine();
                            System.out.println("What is the name of the order?");
                            String ordername = console.nextLine();
                            Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                            if (order != null) {
                                order.setValidated(true);
                                System.out.println("The order for user " + username + " with an order name of " + ordername +
                                        " has been successfully validated");
                            } else {
                                System.out.println("No order found for " + username + " with a name of " + ordername);
                            }
                        } else {
                            System.out.println("You must be a pharmacist to validate an order!");
                        }
                    } else {
                        System.out.println("You must be logged in to validate an order!");
                    }
                } else if (currentInput.equals("load")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            System.out.println("What is the username of the client that the order is for?");
                            String username = console.nextLine();
                            System.out.println("What is the name of the order?");
                            String ordername = console.nextLine();
                            Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                            if (order != null) {
                                ppm.loadOrder(order);
                                System.out.println("Loaded the order to the PPM!");
                            } else {
                                System.out.println("No order found for user " + username + " with an order name of " + ordername);
                            }
                        } else {
                            System.out.println("You must be an employee to load an order to the PPM!");
                        }
                    } else {
                        System.out.println("You must be logged in to load an order to the PPM!");
                    }
                } else if (currentInput.equals("listloadedorders")) {
                    List<Order> orders = ppm.getLoadedOrders();
                    if (orders.size() != 0) {
                        System.out.println("Orders that are currently in the PPM:");
                        for (int i = 0; i < orders.size(); i++) {
                            System.out.println(orders.get(i).orderDetails());
                        }
                    } else {
                        System.out.println("No orders are currently loaded to the PPM");
                    }
                } else if (currentInput.equals("listorders")) {
                    if (ppm.activeUser != null) {
                        if (ppm.activeUser.getType().equals("client")) {
                            List<Order> orders = ppm.dbConnection.getOrdersByUsername(ppm.activeUser.username);
                            if (orders.size() != 0) {
                                System.out.println("Your current orders:");
                                for (int i = 0; i < orders.size(); i++) {
                                    System.out.println(orders.get(i).orderDetails());
                                }
                            } else {
                                System.out.println("You don't have any available orders!");
                            }
                        } else {
                            System.out.println("An admin does not have orders!");
                        }
                    } else {
                        System.out.println("You don't have any orders because you're not logged in!");
                    }
                } else if (currentInput.equals("pay")) {
                    if (ppm.activeUser != null) {
                        if (ppm.activeUser.getType().equals("client")) {
                            System.out.println("Enter the name of the order you want to pay for:");
                            String orderName = console.nextLine();
                            Order order = ppm.dbConnection.getOrderByNameAndUsername(orderName, ppm.activeUser.username);
                            if (order != null) {
                                System.out.println("How will you pay for the order?\n" +
                                        "Options: credit, debit, balance (account balance), cash");
                                String paymentMethod = console.nextLine();

                                order.payOrder(paymentMethod, order.price);
                                System.out.println("The order has been successfully paid off!");
                            } else {
                                System.out.println("No order named " + orderName + " exists for the user named " + ppm.activeUser.username);
                            }
                        } else {
                            System.out.println("Only clients can pay for orders!");
                        }
                    } else {
                        System.out.println("Log in before trying to pay for an order!");
                    }
                } else if (currentInput.equals("addfunds")) {
                    if (ppm.activeUser != null) {
                        if (ppm.activeUser.getType().equals("client")) {
                            System.out.println("How much are you adding to your account?");
                            String amountIn = console.nextLine();
                            System.out.println("How will you add funds?\n" +
                                    "Options: credit, debit, balance (account balance), cash");
                            String paymentMethod = console.nextLine();

                            double amount = Double.parseDouble(amountIn);
                            if (amount >= 0) {
                                ppm.activeUser.balance += amount;
                                System.out.println("Successfully added funds of $" + amountIn + " to your account");
                            } else {
                                System.out.println("You cannot subtract funds from your account!");
                            }
                        } else {
                            System.out.println("There's no reason for an admin to add funds to their account!");
                        }
                    } else {
                        System.out.println("Log in before trying to add to your account's balance!");
                    }
                } else if (currentInput.equals("help")) {
                    printCommands();
                } else if (currentInput.equals("report")) {
                    System.out.println("Please give a brief description of the issue");
                    String desc = console.nextLine();
                    System.out.println("Enter the name of the issue");
                    String name = console.nextLine();

                    if (ppm.addIssue(name, desc) != null) {
                        System.out.println("Issue successfully added to the PPM. The Employees will be notified.");
                    } else {
                        System.out.println("Please enter a valid description for the issue");
                    }

                } else if (currentInput.equals("issues")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            String issues = "";
                            for (Issue issue : ppm.issues) {
                                issues += issue.toString() + '\n';
                            }
                            if (!issues.equals("")) {
                                System.out.println("Current and Past Issues:");
                                System.out.print(issues);
                            } else {
                                System.out.println("There are no current issues with the PPM");
                            }
                        } else {
                            System.out.println("You don't have permission to view the PPM's issues!");
                        }
                    } else {
                        System.out.println("Log into an employee account to access the PPM's issues");
                    }
                } else if (currentInput.equals("solveissue")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            System.out.println("Enter the name of the issue that's been solved");
                            String name = console.nextLine();

                            if (ppm.solveIssue(name.toLowerCase())) {
                                System.out.println("The issue named " + name + " is now solved");
                            } else {
                                System.out.println("Invalid issue name!");
                            }
                        } else {
                            System.out.println("Only employees can label issues as solved");
                        }
                    } else {
                        System.out.println("You must log into an employee's account to label an issue as solved!");
                    }

                } else if (currentInput.equals("removeissue")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            System.out.println("Enter the name of the issue you want to remove");
                            String name = console.nextLine();

                            if (ppm.removeIssue(name) != null) {
                                System.out.println("The issue named " + name + " has been successfully deleted");
                            } else {
                                System.out.println("Invalid issue name!");
                            }
                        } else {
                            System.out.println("You do not have permission to remove reported PPM issues");
                        }
                    } else {
                        System.out.println("You must be logged into an employee account to remove an issue!");
                    }

                } else if (currentInput.equals("clearsolvedissues")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            System.out.println("Are you sure you want to clear all solved issues? (Y/N)");
                            String prompt = null;
                            while (prompt == null) {
                                prompt = console.nextLine();
                                if (prompt.toLowerCase().equals("y")) {
                                    ppm.clearSolvedIssues();
                                    System.out.println("All solved issues have been cleared");
                                } else if (prompt.toLowerCase().equals("n")) {
                                    prompt = "escape";
                                } else {
                                    System.out.println("Bad input: try again");
                                    prompt = null;
                                }
                            }
                        } else {
                            System.out.println("You do not have permission to remove reported PPM issues");
                        }
                    } else {
                        System.out.println("You must be logged into an employee account to remove an issue!");
                    }
                } else if (currentInput.equals("clearissues")) {
                    if (ppm.activeUser != null) {
                        if (!ppm.activeUser.getType().equals("client")) {
                            System.out.println("Are you sure absolutely sure you want to clear all issues? This cannot be undone(Y/N)");
                            String prompt = null;
                            while (prompt == null) {
                                prompt = console.nextLine();
                                if (prompt.toLowerCase().equals("y")) {
                                    ppm.clearIssues();
                                    System.out.println("All issues have been cleared");
                                } else if (prompt.toLowerCase().equals("n")) {
                                    prompt = "escape";
                                } else {
                                    System.out.println("Bad input: try again");
                                    prompt = null;
                                }
                            }
                        } else {
                            System.out.println("You do not have permission to remove reported PPM issues");
                        }
                    } else {
                        System.out.println("You must be logged into an employee account to remove an issue!");
                    }
                }
                else {
                    System.out.println("Invalid Input!\n");
                }

            }
        } catch (SQLException e) {
            System.out.println("Error regarding SQL Database");
            e.printStackTrace();
        }
    }
}
