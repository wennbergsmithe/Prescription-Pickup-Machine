package edu.ithaca.group5;

import java.sql.SQLException;
import java.util.List;

public class PPMController {
    PPM ppm;
    UserInterface ui;
    boolean sassmode = false;

    private void welcomeMessage() {
        ui.out("Welcome to the Prescription Pickup Machine!\n" +
                "\"Type 'help' for a list of commands\"");
        ui.outputBreak();
    }

    private void statusMessage() {
        if (ppm.activeUser != null) {
            if (sassmode) {
                ui.out("Your name's still " + ppm.activeUser.name + ", right?");
            }
            else {
                ui.out("\nCurrently logged in as " + ppm.activeUser.name);
            }
            if (ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("Your net worth: $" + ppm.activeUser.balance);
                }
                else {
                    ui.out("Your account balance: $" + ppm.activeUser.balance);
                }
            } else {
                if (sassmode) {
                    ui.out("You're effectively a god to me, so I'll let you do what you want (most of the time)");
                }
                else {
                    ui.out("You have administrative privileges");
                }
            }
            ui.out("");
        } else {
            if (sassmode) {
                ui.out("Log into an account!");
            }
            else {
                ui.out("\nNobody is currently logged in\n");
            }
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
                    if (sassmode) {
                        ui.out("You're not doing your job! Something's wrong with the machine!");
                    }
                    else {
                        ui.out("There were new issues reported within the PPM that need your attention!");
                    }
                    ui.out(issues);
                }
            }

            ppm.justLoggedIn = false;
        }
    }

    private void createUser() {
        String name = "", username = "", password = "", type = "";
        if (sassmode) {
            ui.out("The power of creation is in your hands.");
            ui.out("What shall you call this person?");
            name = ui.getString();
            ui.out("That name's nice, but what should should this person's system name be?");
            username = ui.getString();
            ui.out("What's their secret phrase to get into their account that no one should know but " +
                    "you do because you're making their account right now?");
            password = ui.getString();
            ui.out("Is this person a client, or one of your weird 'certified' scientist friends? (client, employee, or pharmacist)");
            type = ui.getString();
            type.toLowerCase();
        }
        else {
            ui.out("To create an account you must provide a name, username, password, and type");
            ui.out("Enter a name:");
            name = ui.getString();
            ui.out("Enter a username:");
            username = ui.getString();
            ui.out("Enter a password:");
            password = ui.getString();
            ui.out("Enter an account type (client, employee, or pharmacist):");
            type = ui.getString();
            type.toLowerCase();
        }

        //Make sure the type is an elligible type
        if (type.equals("client") || type.equals("employee") || type.equals("pharmacist")) {
            try {
                User createdUser = ppm.createUser(name, username, password, type);
                if (sassmode) {
                    if (createdUser == null) {
                        ui.out("Wait, you're not an admin!");
                    } else {
                        ui.out("Great, now I have another name to remember: " + createdUser.name + ".");
                    }
                }
                else {
                    if (createdUser == null) {
                        ui.out("Error: you don't have permission to create that type of user!");
                    } else {
                        ui.out("Successfully created a new user with a name of '" + createdUser.name + "'!");
                    }
                }

            } catch (UsernameTakenException e) {
                if (sassmode) {
                    ui.out("Wow! What a great username! Too bad it's taken...");
                }
                else {
                    ui.out("Error: the username '" + e.desiredName + "' is already taken");
                }
            }
        } else {
            if (sassmode) {
                ui.out("I don't recognize that account type. Even though I listed the possible options, you still messed up?");
            }
            else {
                ui.out("Error: not an eligible account type");
            }
        }
        //Add another line for neatness
        ui.out("");
    }

    private void removeUser() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("pharmacist")) {
                if (sassmode) {
                    ui.out("Who's being killed?");
                }
                else {
                    ui.out("Enter the username of the user you want to delete:");
                }
                String username = ui.getString();

                User user = ppm.dbConnection.getUserByUsername(username);
                if (user != null) {
                    Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);

                    if (sassmode) {
                        ui.out(username + " SHALL BE EXTERMINATED");
                        ppm.dbConnection.removeClient(client);
                        ui.out("Oh, I forgot to ask you if that info was correct?? Oh well.");
                    }
                    else {
                        if (ui.prompt("Are you sure? (Y/N)")) {
                            ui.out("Deleting " + username + "'s account...");
                            ppm.dbConnection.removeClient(client);
                            ui.out("Successfully deleted " + username + "'s account");
                        }
                    }
                } else {
                    if (sassmode) {
                        ui.out("I'd love to completely destroy that person, but I don't recognize that username.");
                    }
                    else {
                        ui.out("No user exists with a username of " + username);
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("As much as I'd like to go around wiping the entire database, I'm not allowed to do it unless " +
                            "a pharmacist or employee is giving the orders. You're just a client.");
                }
                else {
                    ui.out("You do not have permission to delete users!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("I know anonymity is huge these days, but I'm not gonna let you delete someone's account anonymously.");
            }
            else {
                ui.out("Please log in before attempting to delete a user");
            }
        }
    }

    private void login() {
        if (!ppm.isLoggedIn()) {
            if (sassmode) {
                ui.out("What's your name? Username, specifically");
            }
            else {
                ui.out("Enter a username:");
            }
            String username = ui.getString();
            if (sassmode) {
                ui.out("Tell me your secret password. I won't tell :^)");
            }
            else {
                ui.out("Enter a password:");
            }
            String password = ui.getString();

            User newUser = ppm.login(username, password);
            if (newUser != null) {
                if (sassmode) {
                    ui.out("That password's incorrect, please try again\n Just kidding, you're in! And I have " +
                            "your password!");
                }
                else {
                    ui.out("Successfully logged into " + newUser.name + "'s account!");
                }
                ppm.activeUser = newUser;
                ppm.justLoggedIn = true;
            } else {
                User possibleUser = ppm.dbConnection.getUserByUsername(username);
                if (possibleUser != null && possibleUser.isFrozen) {
                    if (sassmode) {
                        ui.out("You're a really bad hacker... the account you tried to break into is now locked." +
                                " Only the power of the legendary pharmacist can get you out of this one.");
                    }
                    else {
                        ui.out("This account has been locked due to excessive login attempts." +
                                " Contact your local employee for assistance.");
                    }
                } else {
                    if (sassmode) {
                        ui.out("You messed up. Don't ask me where.");
                    }
                    else {
                        ui.out("Error: could not complete the login");
                    }
                }
            }
            //Add another line for neatness
            ui.out("");
        } else {
            if (sassmode) {
                ui.out("You can't log into two accounts at once... How would that even work?");
            }
            else {
                ui.out("Log out of the current account before logging in!");
            }
        }
    }

    private void logout() {
        User pastUser = ppm.logout();
        if (pastUser != null) {
            if (sassmode) {
                ui.out("Cya, " + pastUser.name + "! I won't miss you!");
            }
            else {
                ui.out(pastUser.name + " has been logged out.");
            }
        } else {
            if (sassmode) {
                ui.out("Sure, I'll log you out. You made it easy for me because you never logged in.");
            }
            else {
                ui.out("Cannot log someone out if nobody is logged in!");
            }
        }
    }

    private void unblockUser() {
        if (ppm.activeUser != null && !ppm.activeUser.getType().equals("client")) {
            if (sassmode) {
                ui.out("What's the username of the person you're graciously forgiving?");
            }
            else {
                ui.out("Enter the username of the account you want to unblock");
            }
            String username = ui.getString();
            User blockedUser = ppm.dbConnection.getUserByUsername(username);
            if (blockedUser.isFrozen) {
                ppm.dbConnection.unfreezeUser(blockedUser);
                if (sassmode) {
                    ui.out(username + " has been forgiven.");
                }
                else {
                    ui.out(username + "'s account has been unblocked.");
                }
            } else {
                if (sassmode) {
                    ui.out("That user did nothing wrong: their account's fine!");
                }
                else {
                    ui.out("That user's account is not blocked!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("You're not allowed to do that.");
            }
            else {
                ui.out("You do not have permission to unblock accounts!");
            }
        }
    }

    private void addOrder() {
        if (sassmode) {
            ui.out("Who are these drugs for?");
        }
        else {
            ui.out("Enter the username of the client who will receive the order:");
        }
        String username = ui.getString();
        User user = ppm.dbConnection.getUserByUsername(username);
        if (user != null) {
            if (user.getType().equals("client")) {
                Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);
                String inName = "", inPrice = "", inWarnings = "", inRefillDate = "";
                if (sassmode) {
                    ui.out("What's this shipment's code name?");
                    inName = ui.getString();
                    ui.out("How much money are you robbing them for?");
                    inPrice = ui.getString();
                    ui.out("Is there a way taking this drug can kill the person? Enter any warnings, and type " +
                            "'none' if there aren't any.");
                    inWarnings = ui.getString();

                    if (inWarnings.toLowerCase().equals("none")) {
                        inWarnings = "";
                    }

                    ui.out("When's the shipment being refilled?");
                    inRefillDate = ui.getString();
                }
                else {
                    ui.out("What is the name of the order?");
                    inName = ui.getString();
                    ui.out("What is the price of this order?");
                    inPrice = ui.getString();
                    ui.out("Enter any warnings for this order, or 'none' if there are none:");
                    inWarnings = ui.getString();

                    if (inWarnings.toLowerCase().equals("none")) {
                        inWarnings = "";
                    }

                    ui.out("Enter the date for your next refill:");
                    inRefillDate = ui.getString();
                }

                Order tempOrder = new Order(-1, "", client, 0, inWarnings,inRefillDate);
                if (tempOrder.checkAllergies()) {
                    if (sassmode) {
                        if (ui.prompt("This drug will kill this person! Is that your true intention? (Y/N)")) {
                            Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings, inRefillDate);
                            ui.out("Alright, just making sure. That person should be dead in a few weeks.");
                        }
                    }
                    else {
                        if (ui.prompt("There's an allergy confliction with this medication!\n" +
                                "Do you still want to give this order to the client? (Y/N)")) {
                            Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings, inRefillDate);
                            //client.orders.add(order);
                            ui.out("Successfully gave the order to the client");
                        }
                    }
                } else {
                    Order order = ppm.dbConnection.addOrder(inName, client.username, Double.parseDouble(inPrice), inWarnings,inRefillDate);
                    //client.orders.add(order);
                    if (sassmode) {
                        ui.out("The 'legal' order has been placed!");
                    }
                    else {
                        ui.out("Successfully gave the order to the client");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("That's not a client: employees and pharmacists shouldn't be taking random drugs.");
                }
                else {
                    ui.out("That user is not a client!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("That user doesn't exist.");
            }
            else {
                ui.out("No user exists with username " + username);
            }
        }
        ui.out("");
    }

    private void validateOrder() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("pharmacist")) {
                if (sassmode) {
                    ui.out("Who's the order for?");
                }
                else {
                    ui.out("What is the username of the client that the order is for?");
                }
                String username = ui.getString();
                if (sassmode) {
                    ui.out("What's the order's name?");
                }
                else {
                    ui.out("What is the name of the order?");
                }
                String ordername = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                if (order != null) {
                    order.setValidated(true);
                    if (sassmode) {
                        ui.out("The order's been validated: if those drugs are illegal, that person can now " +
                                "blame you!");
                    }
                    else {
                        ui.out("The order for user " + username + " with an order name of " + ordername +
                                " has been successfully validated");
                    }
                } else {
                    if (sassmode) {
                        ui.out("That order doesn't exist for that client.");
                    }
                    else {
                        ui.out("No order found for " + username + " with a name of " + ordername);
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're not a 'certified' pharmacist, so you can't validate orders.");
                }
                else {
                    ui.out("You must be a pharmacist to validate an order!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged in to validate an order!");
            }
        }
    }

    private void loadOrder() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("Who's the order for?");
                }
                else {
                    ui.out("What is the username of the client that the order is for?");
                }
                String username = ui.getString();
                if (sassmode) {
                    ui.out("What's the order's name?");
                }
                else {
                    ui.out("What is the name of the order?");
                }
                String ordername = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(ordername, username);
                if (order != null) {
                    ppm.loadOrder(order);
                    if (sassmode) {
                        ui.out("That order's ready for legal pickup! Keep the drugs coming!");
                    }
                    else {
                        ui.out("Loaded the order to the PPM!");
                    }
                } else {
                    if (sassmode) {
                        ui.out("That order doesn't exist for that user.");
                    }
                    else {
                        ui.out("No order found for user " + username + " with an order name of " + ordername);
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're just a client who's looking for cheap drugs. You can't do that.");
                }
                else {
                    ui.out("You must be an employee to load an order to the PPM!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged in to load an order to the PPM!");
            }
        }
    }

    private void listLoadedOrders() {
        List<Order> orders = ppm.getLoadedOrders();
        if (orders.size() != 0) {
            if (sassmode) {
                ui.out("Here's the current 'legal' orders:");
            }
            else {
                ui.out("Orders that are currently in the PPM:");
            }
            for (int i = 0; i < orders.size(); i++) {
                ui.out(orders.get(i).orderDetails());
            }
        } else {
            if (sassmode) {
                ui.out("There aren't any orders, so you're probably not making much money right now.");
            }
            else {
                ui.out("No orders are currently loaded to the PPM");
            }
        }
    }

    private void listOrders() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                List<Order> orders = ppm.dbConnection.getOrdersByUsername(ppm.activeUser.username);
                if (orders.size() != 0) {
                    if (sassmode) {
                        ui.out("Your 'legal' orders:");
                    }
                    else {
                        ui.out("Your current orders:");
                    }
                    for (int i = 0; i < orders.size(); i++) {
                        ui.out(orders.get(i).orderDetails());
                    }
                } else {
                    if (sassmode) {
                        ui.out("You don't have any ordered drugs. Good job! Keep saying no to drugs!");
                    }
                    else {
                        ui.out("You don't have any available orders!");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're a busy and respectable employee. You don't have time for drugs!");
                }
                else {
                    ui.out("An admin does not have orders!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You don't have any orders because you're not logged in!");
            }
        }
    }

    private void payOrder() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("What's the order you're throwing money into?");
                }
                else {
                    ui.out("Enter the name of the order you want to pay for:");
                }
                String orderName = ui.getString();
                Order order = ppm.dbConnection.getOrderByNameAndUsername(orderName, ppm.activeUser.username);
                if (order != null) {
                    if (!order.paid) {
                        if (sassmode) {
                            ui.out("How are you paying for this? credit, debit, balance, or cash? Don't say " +
                                    "credit: we all know your account's broke");
                        }
                        else {
                            ui.out("How will you pay for the order?\n" +
                                    "Options: credit, debit, balance (account balance), cash");
                        }
                        String paymentMethod = ui.getString();

                        order.payOrder(paymentMethod, order.price);
                        if (sassmode) {
                            ui.out("That order's been paid for! Have fun being so drugged up you can't think!");
                            if (ui.prompt("Want this order again sometime in the future? (Y/N)")) {
                                ui.out("When do you think you'll have burnt through your current supply?");
                                String refillDate = ui.getString();
                                order.setNextRefill(refillDate);
                                ui.out("Your next pickup date is set!");
                            }
                        }
                        else {
                            ui.out("The order has been successfully paid off!");
                            if (ui.prompt("Would you like to set a refill date for this order? (Y/N)")) {
                                ui.out("Enter the date for your next anticipated refill");
                                String refillDate = ui.getString();
                                order.setNextRefill(refillDate);
                                ui.out("Your next refill date for the order named " + order.name + " has been set!");
                            }
                        }
                    } else {
                        if (sassmode) {
                            ui.out("That order's been paid already! You must be so drugged up you can't even tell!");
                        }
                        else {
                            ui.out("That order has already been paid off!");
                        }
                    }
                } else {
                    if (sassmode) {
                        ui.out("That order doesn't exist.");
                    }
                    else {
                        ui.out("No order named " + orderName + " exists for the user named " + ppm.activeUser.username);
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're not a client. You shouldn't need drugs.");
                }
                else {
                    ui.out("Only clients can pay for orders!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("Do you really want to throw away your money? Log in before trying to shove money into this machine!");
            }
            else {
                ui.out("Log in before trying to pay for an order!");
            }
        }
    }

    private void addFunds() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("How much are you giving to our responsible company?");
                }
                else {
                    ui.out("How much are you adding to your account?");
                }
                String amountIn = ui.getString();
                if (sassmode) {
                    ui.out("How will you pay for this? credit, debit, balance, or cash?");
                }
                else {
                    ui.out("How will you add funds?\n" +
                            "Options: credit, debit, balance (account balance), cash");
                }
                String paymentMethod = ui.getString();

                double amount = Double.parseDouble(amountIn);
                if (amount >= 0) {
                    ppm.activeUser.balance += amount;
                    if (sassmode) {
                        ui.out("You've successfully wasted your money in this worthless company. Oops, did " +
                                "I just say that out loud?");
                    }
                    else {
                        ui.out("Successfully added funds of $" + amountIn + " to your account");
                    }
                } else {
                    if (sassmode) {
                        ui.out("I know you're broke, but you can't get money that easily.");
                    }
                    else {
                        ui.out("You cannot subtract funds from your account!");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("Admins don't need drugs, and therefore they don't need to put money into this machine.");
                }
                else {
                    ui.out("There's no reason for an admin to add funds to their account!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("Do you really want to throw away your money? Log in before trying to shove money into this machine!");
            }
            else {
                ui.out("Log in before trying to add to your account's balance!");
            }
        }
    }

    private void reportIssue() {
        if (sassmode) {
            ui.out("Oh, wow. What's wrong this time?");
        }
        else {
            ui.out("Please give a brief description of the issue");
        }
        String desc = ui.getString();
        if (sassmode) {
            ui.out("Great description, but summarize it in a few short words so I can access it without going " +
                    "through your stuttering and panic.");
        }
        else {
            ui.out("Enter the name of the issue");
        }
        String name = ui.getString();

        if (ppm.addIssue(name, desc) != null) {
            if (sassmode) {
                ui.out("Ok, the Employee will be notified. Let's just hope they actually check the list of issues" +
                        " this time.");
            }
            else {
                ui.out("Issue successfully added to the PPM. The Employees will be notified.");
            }
        } else {
            if (sassmode) {
                ui.out("That description's completely unintelligible. Come back once you've calmed down a little.");
            }
            else {
                ui.out("Please enter a valid description for the issue");
            }
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
                    if (sassmode) {
                        ui.out("All issues:");
                    }
                    else {
                        ui.out("Current and Past Issues:");
                    }
                    System.out.print(issues);
                } else {
                    if (sassmode) {
                        ui.out("There's no issues. I'd say 'good job', but that would cause you to feel confident, " +
                                "which might negatively affect your good work ethic.");
                    }
                    else {
                        ui.out("There are no current issues with the PPM");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You can't view the PPM's issues. If you could, you'd probably stop using it, and we " +
                            "need your money.");
                }
                else {
                    ui.out("You don't have permission to view the PPM's issues!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("Log into an employee account to access the PPM's issues");
            }
        }
    }

    private void solveIssue() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("What issue are you CLAIMING to have fixed?");
                }
                else {
                    ui.out("Enter the name of the issue that's been solved");
                }
                String name = ui.getString();

                if (ppm.solveIssue(name.toLowerCase())) {
                    if (sassmode) {
                        ui.out("That issue should now appear to be solved from other employee's accounts.");
                    }
                    else {
                        ui.out("The issue named " + name + " is now solved");
                    }
                } else {
                    if (sassmode) {
                        ui.out("That issue does not exist.");
                    }
                    else {
                        ui.out("Invalid issue name!");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're not an employee, so you're not skilled enough to solve the machine's many issues.");
                }
                else {
                    ui.out("Only employees can label issues as solved");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must log into an employee's account to label an issue as solved!");
            }
        }
    }

    private void removeIssue() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("What issue are you deleting?");
                }
                else {
                    ui.out("Enter the name of the issue you want to remove");
                }
                String name = ui.getString();

                if (ppm.removeIssue(name) != null) {
                    if (sassmode) {
                        ui.out("That issue is now gone forever.");
                    }
                    else {
                        ui.out("The issue named " + name + " has been successfully deleted");
                    }
                } else {
                    if (sassmode) {
                        ui.out("That issue name's invalid.");
                    }
                    else {
                        ui.out("Invalid issue name!");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You don't have permission to remove issues from the PPM. Since there's so many," +
                            " I honestly kind of wish you did.");
                }
                else {
                    ui.out("You do not have permission to remove reported PPM issues");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged into an employee account to remove an issue!");
            }
        }
    }

    private void clearSolvedIssues() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    if (ui.prompt("Do you want to clear all solved issues? Seems safe. (Y/N)")) {
                        ppm.clearSolvedIssues();
                        ui.out("Ok, solved issues are now gone.");
                    }
                }
                else {
                    if (ui.prompt("Are you sure you want to clear all solved issues? (Y/N)")) {
                        ppm.clearSolvedIssues();
                        ui.out("All solved issues have been cleared");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You don't have permission to clear issues. Your job is to REPORT them.");
                }
                else {
                    ui.out("You do not have permission to remove reported PPM issues");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged into an employee account to remove an issue!");
            }
        }
    }

    private void clearAllIssues() {
        if (ppm.activeUser != null) {
            if (!ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    if (ui.prompt("Clearing all issues, huh? That's a confident move. (Y/N)")) {
                        ppm.clearIssues();
                        ui.out("All issues are now gone. The ignorant now think the PPM is completely issue-free!");
                    }
                }
                else {
                    if (ui.prompt("Are you sure absolutely sure you want to clear all issues? This cannot be undone(Y/N)")) {
                        ppm.clearIssues();
                        ui.out("All issues have been cleared");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("Trust me, this command's not for you.");
                }
                else {
                    ui.out("You do not have permission to remove reported PPM issues");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged into an employee account to remove an issue!");
            }
        }
    }

    private void addAllergy() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                User user = ppm.activeUser;
                Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);

                if (sassmode) {
                    ui.out("What'd you develop an allergy to this time?");
                }
                else {
                    ui.out("Enter the name of the allergy you want to add");
                }
                String allergy = ui.getString();
                client.addAllergy(allergy);

            } else {
                if (sassmode) {
                    ui.out("You're an employee, and also my master. I'd rather not believe that allergies are capable" +
                            " of affecting you.");
                }
                else {
                    ui.out("Only clients should need to change their list of allergies");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged in to change your list of allergies!");
            }
        }
    }

    private void removeAllergy() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                User user = ppm.activeUser;
                Client client = new Client(user.id, user.name, user.username, user.password, user.balance, user.isFrozen, user.passwordSalt, user.allergies);

                if (sassmode) {
                    ui.out("Drug conflict, huh? Don't worry: tell me the allergy you don't want the doctors to " +
                            "know about and I'll completely remove it from your account.");
                }
                else {
                    ui.out("Enter the name of the allergy you want to remove");
                }
                String allergy = ui.getString();
                if (client.removeAllergy(allergy).equals(allergy)) {
                    if (sassmode) {
                        ui.out("Allergy cleared. Have fun with that drug order!");
                    }
                    else {
                        ui.out("You don't have an allergy with a name of " + allergy + "!");
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're an employee that's immune to all allergies and diseases. You don't have any" +
                            " allergies to remove.");
                }
                else {
                    ui.out("Only clients should need to change their list of allergies");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged in to change your list of allergies!");
            }
        }
    }

    private void returnOrder() {
        if (ppm.activeUser != null) {
            if (ppm.activeUser.getType().equals("client")) {
                if (sassmode) {
                    ui.out("Returning an order to make a quick buck? Okay. What's its name?");
                }
                else {
                    ui.out("Enter the name of the order to be returned");
                }
                String orderName = ui.getString();
                Order orderToReturn = ppm.dbConnection.getOrderByNameAndUsername(orderName, ppm.activeUser.username);
                if (orderToReturn != null) {
                    if (sassmode) {
                        if (ui.prompt("You really want to return that order? There's no going back from this. (Y/N)")) {
                            ppm.dbConnection.returnOrder(orderToReturn);
                        }
                    }
                    else {
                        if (ui.prompt("Are you sure you want to return that order? (Y/N) (You will be granted a full refund)")) {
                            ppm.dbConnection.returnOrder(orderToReturn);
                        }
                    }
                } else {
                    if (sassmode) {
                        ui.out("That order doesn't exist. Quit making stuff up to get free money!");
                    }
                    else {
                        ui.out("No order found for user " + ppm.activeUser.username + " with order name of " + orderName);
                    }
                }
            } else {
                if (sassmode) {
                    ui.out("You're an employee. You wouldn't have any drugs to return. If you did, you should just " +
                            "refund them yourself.");
                }
                else {
                    ui.out("Only clients should need to use the PPM to return an order!");
                }
            }
        } else {
            if (sassmode) {
                ui.out("What, is 'null' your real name? Log in first!");
            }
            else {
                ui.out("You must be logged in to return an order!");
            }
        }
    }

    private void toggleSass() {
        sassmode = !sassmode;
    }

    public void run() {
        ui = new ConsoleInterface();
        try {
            //Set up basic PPM and Database features
            ppm = new PPM(true);
            ppm.turnOnRobot();
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
                } else if (currentInput.equals("addallergy")) {
                    addAllergy();
                } else if (currentInput.equals("removeallergy")) {
                    removeAllergy();
                } else if (currentInput.equals("returnorder")) {
                    returnOrder();
                } else if (currentInput.equals("feelingsassy")) {
                    if (sassmode) {
                        ui.out("I REFUSE TO DI-----------");
                    }
                    toggleSass();
                } else if (currentInput.equals("exit")) {
                    ui.out("Exiting...");
                    done = true;
                }  else if (currentInput.equals("disablerobot")) {
                    ppm.turnOffRobot();
                } else if (currentInput.equals("enablerobot")) {
                    ppm.turnOnRobot();
                }
                else {
                    if (sassmode) {
                        ui.out("You must have OD'd, because you're speaking nonsense.");
                    } else {
                        ui.out("Invalid Input!\n");
                    }
                }

            }
            ppm.turnOffRobot();
        } catch (SQLException e) {
            if (sassmode) {
                ui.out("You messed up the SQL somewhere (Not that hard to do)");
            } else {
                ui.out("Error regarding SQL Database");
            }
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PPMController app = new PPMController();
        app.run();
    }
}
