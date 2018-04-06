package edu.ithaca.group5;

public interface DBConnector {

    /**
     * Adds employee to the database.
     * @param employee
     */
    void addEmployee(Employee employee);

    /**
     * Adds pharamacist to the database.
     * @param pharmacist
     */
    void addPharmacist(Pharmacist pharmacist);

    /**
     * Adds client to the database.
     * @param client
     */
    void addClient(Client client);


    /**
     * removes a client from the database
     * @param clientToRemove
     */
    Client removeClient(Client clientToRemove);


    /**
     * checks to see if the specified client is in the database
     * @param toCheck
     * @return
     */
    boolean isInDB(Client toCheck);


    User getUserByUsernameAndPassword(String username, String password);

    /**
     * empties user table.
     */
    void emptyUserTable();

    /**
     * empties order table.
     */
    void emptyOrderTable();



}
