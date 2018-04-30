package edu.ithaca.group5;

import java.sql.SQLException;
import java.util.List;

public class PPMController {
    PPM ppm;
    UserInterface ui;

    private void welcomeMessage() {
        ui.out("Welcome to the Prescription Pickup Machine!\n" +
                "\"Type 'help' for a list of commands\"");
        ui.outputBreak();
    }

    private void statusMessage() {
        if (ppm.activeUser != null) {
            ui.out("\nCurrently logged in as " + ppm.activeUser.name);
            if (ppm.activeUser.getType().equals("client")) {
                ui.out("Your account balance: $" + ppm.activeUser.balance);
            } else {
                ui.out("You have administrative privileges");
            }
            ui.out("");
        } else {
            ui.out("\nNobody is currently logged in\n");
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
                    ui.out("There were new issues reported within the PPM that need your attention!");
                    ui.out(issues);
                }
            }

            ppm.justLoggedIn = false;
        }
    }

    private void createUser() {
        ui.out("To create an account you must provide a name, username, password, and type");
        ui.out("Enter a name:");
        String name = ui.getString();
        ui.out("Enter a username:");
        String username = ui.getString();
        ui.out("Enter a password:");
        String password = ui.getString();
        ui.out("Enter an account type (client, employee, or pharmacist):");
        String type = ui.getString();
        type.toLowerCase();

        //Make sure the type is an elligible type
        if (type.equals("client") || type.equals("employee") || type.equals("pharmacist")) {
            try {
                User createdUser = ppm.createUser(name, username, password, type);
                if (createdUser == null) {
                    ui.out("Error: you don't have permission to create that type of user!");
                } else {
                    ui.out("Successfully created a new user with a name of '" + createdUser.name + "'!");
                }

            } catch (UsernameTakenException e) {
                ui.out("Error: the username '" + e.desiredName + "' is already taken");
            }
        } else {
            ui.out("Error: not an eligible account type");
        }
        //Add another line for neatness
        ui.out("");
    }

    private void removeUser() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("pharmacist")) {
                ui.out("Enter the username of the user you want to delete:");
                String username = ui.getString();

                User user = ppm.dbConnection.getUserByUsername(username);
                if (user != null) {
                    Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);
                    boolean response = ui.prompt("Are you sure? (Y/N)");

                    if (response) {
                        ui.out("Deleting " + username + "'s account...");
                        ppm.dbConnection.removeClient(client);
                        ui.out("Successfully deleted " + username + "'s account");
                    }
                } else {
                    ui.out("No user exists with a username of " + username);
                }
            } else {
                ui.out("You do not have permission to delete users!");
            }
        } else {
            ui.out("Please log in before attempting to delete a user");
        }
    }

    private void login() {
        if (!ppm.isLoggedIn()) {
            ui.out("Enter a username:");
            String username = ui.getString();
            ui.out("Enter a password:");
            String password = ui.getString();

            User newUser = ppm.login(username, password);
            if (newUser != null) {
                ui.out("Successfully logged into " + newUser.name + "'s account!");
                ppm.activeUser = newUser;
                ppm.justLoggedIn = true;
            } else {
                User possibleUser = ppm.dbConnection.getUserByUsername(username);
                if (possibleUser != null && possibleUser.isFrozen) {
                    ui.out("This account has been locked due to excessive login attempts." +
                            " Contact your local employee for assistance.");
                } else {
                    ui.out("Error: could not complete the login");
                }
            }
            //Add another line for neatness
            ui.out("");
        } else {
            ui.out("Log out of the current account before logging in!");
        }
    }

    private void logout() {
        User pastUser = ppm.logout();
        if (pastUser != null) {
            ui.out(pastUser.name + " has been logged out.");
        } else {
            ui.out("Cannot log someone out if nobody is logged in!");
        }
    }

    private void unblockUser() {
        if (ppm.activeUser != null && !ppm.activeUser.getType().equals("client")) {
            ui.out("Enter the username of the account you want to unblock");
            String username = ui.getString();
            User blockedUser = ppm.dbConnection.getUserByUsername(username);
            if (blockedUser.isFrozen) {
                ppm.dbConnection.unfreezeUser(blockedUser);
                ui.out(username + "'s account has been unblocked.");
            } else {
                ui.out("That user's account is not blocked!");
            }
        } else {
            ui.out("You do not have permission to unblock accounts!");
        }
    }

    private void addOrder() {
        ui.out("Enter the username of the client who will receive the order:");
        String username = ui.getString();
        User user = ppm.dbConnection.getUserByUsername(username);
        if (user != null) {
            if (user.getType().equals("client")) {
                Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);
                ui.out("What is the name of the order?");
                String inName = ui.getString();
                ui.out("What is the price of this order?");
                String inPrice = ui.getString();
                ui.out("Enter any warnings for this order, or 'none' if there are none:");
                String inWarnings = ui.getString();
                if (inWarnings.toLowerCase().equals("none")) {
                    inWarnings = "";
                }
                ui.out("Enter the date for your next refill:");
                String inRefillDate = ui.getString();

                Order tempOrder = new Order(-1, "", client, 0, inWarnings,inRefillDate);
                if (tempOrder.checkAllergies()) {
                    boolean response = ui.prompt("There's an allergy confliction with this medication!\n" +
                            "Do you still want to give this order to the client? (Y/N)");
                    if (response) {
                            Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings,inRefillDate);
                            client.orders.add(order);
                            ui.out("Successfully gave the order to the client");
                    }
                } else {
                    Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings,inRefillDate);
                    client.orders.add(order);
                    ui.out("Successfully gave the order to the client");
                }
            } else {
                ui.out("That user is not a client!");
            }
        } else {
            ui.out("No user exists with username " + username);
        }
        ui.out("");
    }

    private void validateOrder() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("pharmacist")) {
                ui.out("What is the username of the client that the order is for?");
                String username = ui.getString();
                ui.out("What is the name of the order?");
                String ordername = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                if (order != null) {
                    order.setValidated(true);
                    ui.out("The order for user " + username + " with an order name of " + ordername +
                            " has been successfully validated");
                } else {
                    ui.out("No order found for " + username + " with a name of " + ordername);
                }
            } else {
                ui.out("You must be a pharmacist to validate an order!");
            }
        } else {
            ui.out("You must be logged in to validate an order!");
        }
    }

    private void loadOrder() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                ui.out("What is the username of the client that the order is for?");
                String username = ui.getString();
                ui.out("What is the name of the order?");
                String ordername = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                if (order != null) {
                    ppm.loadOrder(order);
                    ui.out("Loaded the order to the PPM!");
                } else {
                    ui.out("No order found for user " + username + " with an order name of " + ordername);
                }
            } else {
                ui.out("You must be an employee to load an order to the PPM!");
            }
        } else {
            ui.out("You must be logged in to load an order to the PPM!");
        }
    }

    private void listLoadedOrders() {
        List<Order> orders = ppm.getLoadedOrders();
        if (orders.size() != 0) {
            ui.out("Orders that are currently in the PPM:");
            for (int i = 0; i < orders.size(); i++) {
                ui.out(orders.get(i).orderDetails());
            }
        } else {
            ui.out("No orders are currently loaded to the PPM");
        }
    }

    private void listOrders() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                List<Order> orders = ppm.dbConnection.getOrdersByUsername(ppm.activeUser.username);
                if (orders.size() != 0) {
                    ui.out("Your current orders:");
                    for (int i = 0; i < orders.size(); i++) {
                        ui.out(orders.get(i).orderDetails());
                    }
                } else {
                    ui.out("You don't have any available orders!");
                }
            } else {
                ui.out("An admin does not have orders!");
            }
        } else {
            ui.out("You don't have any orders because you're not logged in!");
        }
    }

    private void payOrder() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                ui.out("Enter the name of the order you want to pay for:");
                String orderName = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(orderName, ppm.activeUser.username);
                if (order != null) {
                    ui.out("How will you pay for the order?\n" +
                            "Options: credit, debit, balance (account balance), cash");
                    String paymentMethod = ui.getString();

                    order.payOrder(paymentMethod, order.price);
                    ui.out("The order has been successfully paid off!");
                } else {
                    ui.out("No order named " + orderName + " exists for the user named " + ppm.activeUser.username);
                }
            } else {
                ui.out("Only clients can pay for orders!");
            }
        } else {
            ui.out("Log in before trying to pay for an order!");
        }
    }

    private void addFunds() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                ui.out("How much are you adding to your account?");
                String amountIn = ui.getString();
                ui.out("How will you add funds?\n" +
                        "Options: credit, debit, balance (account balance), cash");
                String paymentMethod = ui.getString();

                double amount = Double.parseDouble(amountIn);
                if (amount >= 0) {
                    ppm.activeUser.balance += amount;
                    ui.out("Successfully added funds of $" + amountIn + " to your account");
                } else {
                    ui.out("You cannot subtract funds from your account!");
                }
            } else {
                ui.out("There's no reason for an admin to add funds to their account!");
            }
        } else {
            ui.out("Log in before trying to add to your account's balance!");
        }
    }

    private void reportIssue() {
        ui.out("Please give a brief description of the issue");
        String desc = ui.getString();
        ui.out("Enter the name of the issue");
        String name = ui.getString();

        if (ppm.addIssue(name, desc) != null) {
            ui.out("Issue successfully added to the PPM. The Employees will be notified.");
        } else {
            ui.out("Please enter a valid description for the issue");
        }
    }

    private void listIssues() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                String issues = "";
                for (Issue issue : ppm.issues) {
                    issues += issue.toString() + '\n';
                }
                if (!issues.equals("")) {
                    ui.out("Current and Past Issues:");
                    System.out.print(issues);
                } else {
                    ui.out("There are no current issues with the PPM");
                }
            } else {
                ui.out("You don't have permission to view the PPM's issues!");
            }
        } else {
            ui.out("Log into an employee account to access the PPM's issues");
        }
    }

    private void solveIssue() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                ui.out("Enter the name of the issue that's been solved");
                String name = ui.getString();

                if (ppm.solveIssue(name.toLowerCase())) {
                    ui.out("The issue named " + name + " is now solved");
                } else {
                    ui.out("Invalid issue name!");
                }
            } else {
                ui.out("Only employees can label issues as solved");
            }
        } else {
            ui.out("You must log into an employee's account to label an issue as solved!");
        }
    }

    private void removeIssue() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                ui.out("Enter the name of the issue you want to remove");
                String name = ui.getString();

                if (ppm.removeIssue(name) != null) {
                    ui.out("The issue named " + name + " has been successfully deleted");
                } else {
                    ui.out("Invalid issue name!");
                }
            } else {
                ui.out("You do not have permission to remove reported PPM issues");
            }
        } else {
            ui.out("You must be logged into an employee account to remove an issue!");
        }
    }

    private void clearSolvedIssues() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                boolean response = ui.prompt("Are you sure you want to clear all solved issues? (Y/N)");
                if (response) {
                        ppm.clearSolvedIssues();
                        ui.out("All solved issues have been cleared");
                }
            } else {
                ui.out("You do not have permission to remove reported PPM issues");
            }
        } else {
            ui.out("You must be logged into an employee account to remove an issue!");
        }
    }

    private void clearAllIssues() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                boolean response = ui.prompt("Are you sure absolutely sure you want to clear all issues? This cannot be undone(Y/N)");
                if (response) {
                        ppm.clearIssues();
                        ui.out("All issues have been cleared");
                }
            } else {
                ui.out("You do not have permission to remove reported PPM issues");
            }
        } else {
            ui.out("You must be logged into an employee account to remove an issue!");
        }
    }

    public void run() {
        ui = new ConsoleInterface();
        try {
            //Set up basic PPM and Database features
            ppm = new PPM(true);
            ppm.dbConnection.addPharmacist(new Pharmacist(-1, "test pharmacist", "testPharmacist", "password"));
            ppm.login("testPharmacist", "password");

            //Print welcome message
            welcomeMessage();

            String currentInput;
            boolean done = false;
            while (!done) {
                //Print status screen
                statusMessage();

                currentInput = ui.getString();
                if (currentInput.equals("create")) {
                    createUser();
                } else if (currentInput.equals("remove")) {
                    removeUser();
                } else if (currentInput.equals("login")) {
                    login();
                } else if (currentInput.equals("logout")) {
                    logout();
                } else if (currentInput.equals("unblock")) {
                    unblockUser();
                } else if (currentInput.equals("addorder")) {
                    addOrder();
                } else if (currentInput.equals("validate")) {
                    validateOrder();
                } else if (currentInput.equals("load")) {
                    loadOrder();
                } else if (currentInput.equals("listloadedorders")) {
                    listLoadedOrders();
                } else if (currentInput.equals("listorders")) {
                    listOrders();
                } else if (currentInput.equals("pay")) {
                    payOrder();
                } else if (currentInput.equals("addfunds")) {
                    addFunds();
                } else if (currentInput.equals("help")) {
                    ui.displayCommands();
                } else if (currentInput.equals("report")) {
                    reportIssue();
                } else if (currentInput.equals("issues")) {
                    listIssues();
                } else if (currentInput.equals("solveissue")) {
                    solveIssue();
                } else if (currentInput.equals("removeissue")) {
                    removeIssue();
                } else if (currentInput.equals("clearsolvedissues")) {
                    clearSolvedIssues();
                } else if (currentInput.equals("clearissues")) {
                    clearAllIssues();
                } else if (currentInput.equals("exit")) {
                    ui.out("Exiting...");
                    done = true;
                } else {
                    ui.out("Invalid Input!\n");
                }

            }
        } catch (SQLException e) {
            ui.out("Error regarding SQL Database");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PPMController app = new PPMController();
        app.run();
    }
}
