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
