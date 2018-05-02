package edu.ithaca.group5;

import java.util.Scanner;

public class ConsoleInterface implements UserInterface {
    Scanner console;

    public ConsoleInterface() {
        console = new Scanner(System.in);
    }

    @Override
    public void outputBreak() {
        System.out.println("----------------------------------------------------------");
    }

    @Override
    public void displayCommands() {
        outputBreak();
        System.out.println("Current Commands:");
        outputBreak();
        System.out.println("General Commands:\n");
        System.out.println("help - Displays this list of commands");
        System.out.println("login - Log into an existing account");
        System.out.println("logout - Log out of the current account");
        System.out.println("exit - Ends the program");
        System.out.println("listloadedorders - Lists the orders that are currently in the PPM");
        System.out.println("report - Report an issue with the PPM");
        outputBreak();
        System.out.println("Client Commands:\n");
        System.out.println("pay - Pay for an order");
        System.out.println("listorders - List your current orders");
        System.out.println("addfunds - Add funds to your account");
        System.out.println("addallergy - Add an allergy to your current list of allergies");
        System.out.println("removeallergy - Remove an allergy from your current list of allergies");
        System.out.println("returnorder - Return an order and get a refund");
        outputBreak();
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

    @Override
    public void out(String outString) {
        System.out.println(outString);
    }

    @Override
    public boolean prompt(String msg) {
        out(msg);
        String prompt = null;
        while (prompt == null) {
            prompt = getString();
            if (prompt.toLowerCase().equals("y")) {
                return true;
            } else if (prompt.toLowerCase().equals("n")) {
                return false;
            } else {
                out("Bad input: Try again");
                prompt = null;
            }
        }

        return false;
    }

    @Override
    public String getString() {
        String nextString = console.nextLine();
        return nextString;
    }

    @Override
    public double getDouble() {
        double nextDouble = console.nextDouble();
        return nextDouble;
    }

}
