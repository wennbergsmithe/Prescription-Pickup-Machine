package edu.ithaca.group5;

public interface UserInterface {

    /**
     * Shows the user a list of commands
     */
    public void displayCommands();

    /**
     * Creates a break in displayed output
     */
    public void outputBreak();

    /**
     * Displays a message to the user
     * @param outString the message to be displayed
     */
    public void out(String outString);

    /**
     * Prompts the user yes or no
     * @param msg message asked to the user that expects a yes or no answer
     * @return true if yes, false if no
     */
    public boolean prompt(String msg);

    /**
     * Gets a string from the user
     * @return the string the user inputted
     */
    public String getString();

    /**
     * Gets a double from the user
     * @return the double the user inputted
     */
    public double getDouble();
}
