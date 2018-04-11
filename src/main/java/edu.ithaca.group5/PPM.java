package edu.ithaca.group5;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class UsernameTakenException extends Exception {
    String desiredName;
}

public class PPM {
    DBConnector dbConnection;
    User activeUser;
    final int MAX_LOGIN_ATTEMPTS = 3;
    Map<String, Integer> failedLoginAttempts = new HashMap<>();

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
                Client client = new Client(-1, name, username, password);
                dbConnection.addClient(client);
                createdUser = client;
                break;
            case("employee"):
                Employee employee = new Employee(-1, name, username, password);
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
        printBreak();
        System.out.println("Client Commands:\n");
        System.out.println("pay - Pay for an order");
        System.out.println("addfunds - Add funds to your account");
        printBreak();
        System.out.println("Pharmacist/Employee Commands:");
        System.out.println("Keep in mind Employees can only operate on client accounts\n");
        System.out.println("create - Creates a new account");
        System.out.println("remove - Delete a user from the PPM's database");
        System.out.println("unblock - Disable the lock on an account");

    }

    public static void main(String[] args) {
        try {
            PPM ppm = new PPM(true);
            ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test pharmacist", "testPharmacist", "password"));
            User user = ppm.login("testPharmacist", "password");
            Scanner console = new Scanner(System.in);
            System.out.println("Welcome to the Prescription Pickup Machine!");
            printCommands();

            String currentInput;
            boolean done = false;
            while (!done) {
                printBreak();
                if (ppm.activeUser != null) {
                    System.out.println("\nCurrently logged in as " + ppm.activeUser.name + "\n");
                } else {
                    System.out.println("\nNobody is currently logged in\n");
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
                    System.out.println("Enter the username of the user you want to delete:");
                    String username = console.nextLine();

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
                        } else {
                            User possibleUser = ppm.dbConnection.getUserByUsername(username);
                            if (possibleUser != null) {
                                if (possibleUser.isFrozen) {
                                    System.out.println("This account has been locked due to excessive login attempts." +
                                            " Contact your local employee for assistance.");
                                }
                            }
                            System.out.println("Error: could not complete the login");
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
                    }
                    else {
                        System.out.println("Cannot log someone out if nobody is logged in!");
                    }
                } else if (currentInput.equals("exit")) {
                    System.out.println("Exiting...");
                    done = true;
                } else if (currentInput.equals("pay")) {

                } else if (currentInput.equals("addfunds")) {

                } else if (currentInput.equals("help")) {
                    printCommands();
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
