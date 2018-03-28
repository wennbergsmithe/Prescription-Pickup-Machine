package edu.ithaca.group5;

import java.sql.*;
import java.util.Scanner;

class UsernameTakenException extends Exception {
    String desiredName;
}

public class PPM {
    Connection dbConnection;
    User activeUser;

    public PPM(String dbHost, String dbUser, String dbPassword) throws SQLException {
        // needed to register db driver
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        dbConnection = DriverManager.getConnection(dbHost, dbUser, dbPassword);
    }

    /**
     * Logs user into the PPM. If the user doesn't match anything in the db, returns null, otherwise returns User object
     * @param username User's username
     * @param password User's password
     * @return corresponding User, otherwise null
     */
    public User login(String username, String password) {
        try {
            Statement statement = dbConnection.createStatement();
            // TODO: not safe from sql injection right now. Eventually use prepared statements
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    username + "' and password='" + password + "'");
            if (results.next()) {
                switch (results.getString("type")) {
                    case "client":
                        activeUser = new Client(results.getLong("id"), results.getString("name"),
                                results.getString("username"), results.getString("password"));
                        break;
                    case "employee":
                        activeUser = new Employee(results.getLong("id"), results.getString("name"),
                                results.getString("username"), results.getString("password"));
                        break;
                    case "pharmacist":
                        activeUser = new Pharmacist(results.getLong("id"), results.getString("name"),
                                results.getString("username"), results.getString("password"));
                        break;
                    default:
                        return null;
                }
                statement.close();
                return activeUser;

            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

        try {
            Statement statement = dbConnection.createStatement();

            //Make sure the desired username does not already exist
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    username + "'");
            if (results.next()) {
                //Name already exists: throw exception
                UsernameTakenException e = new UsernameTakenException();
                e.desiredName = username;
                throw e;
            }



            //Determine the type of the current active user
            String userType;
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    activeUser.username + "'");
            if (results.next()) {
                userType = results.getString("type");
            } else {
                return createdUser;
            }



            //Make sure the current active user has permission to create this type of account
            if (userType == "client") {
                //Clients cannot make any accounts
                return createdUser;
            } else if (userType == "employee") {
                //Employees cannot create employees or pharmacists
                if (type == "employee") {
                    return createdUser;
                }
                if (type == "pharmacist") {
                    return createdUser;
                }
            }



            //User must not already exist: add them to the database
            statement.execute("INSERT INTO user (name, username, password, type) VALUES ('" + name + "', '"
                    + username + "', '" + password + "', '" + type + "')");

            //Get the user's database ID
            results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    username + "'");
            results.next();
            RowId rowid = results.getRowId(1);
            long id = Integer.parseInt(rowid.toString());

            //Create the user object that will be returned
            createdUser = new Client(id, name, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            PPM ppm = new PPM("jdbc:mysql://localhost:3306/test_dd", "danny", "danny97101");
            ppm.activeUser = null;
            Scanner console = new Scanner(System.in);
            System.out.println("Welcome to the Prescription Pickup Machine!\n" +
                    "Current Commands:\n" +
                    "create - Creates a new account\n" +
                    "login - Log into an existing account\n");

            String currentInput;
            boolean done = false;
            while (!done) {
                currentInput = console.nextLine();
                if (currentInput == "create") {
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
                    if (type == "client" || type == "employee" || type == "pharmacist") {
                        try {
                            User createdUser = ppm.createUser(name, username, password, type);
                            if (createdUser == null) {
                                System.out.println("Error: you don't have permission to create that type of user!");
                            }
                            else {
                                System.out.println("Successfully created a new user with a name of '" + createdUser.name + "'!");
                            }

                        } catch (UsernameTakenException e) {
                            System.out.println("Error: the username '" + e.desiredName + "' is already taken");
                        }
                    }
                    else {
                        System.out.println("Error: not an eligible account type");
                    }
                    //Add another line for neatness
                    System.out.println();
                }
                else if (currentInput == "login") {
                    System.out.println("Enter a username:");
                    String username = console.nextLine();
                    System.out.println("Enter a password:");
                    String password = console.nextLine();

                    User newUser = ppm.login(username, password);
                    if (newUser != null) {
                        System.out.println("Successfully logged into " + newUser.name + "'s account!");
                        ppm.activeUser = newUser;
                    }
                    else {
                        System.out.println("Error: could not complete the login");
                    }
                    //Add another line for neatness
                    System.out.println();
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
