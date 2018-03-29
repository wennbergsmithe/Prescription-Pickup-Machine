package edu.ithaca.group5;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

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
                    case "client":      activeUser = new Client(results.getLong("id"), results.getString("name"),
                                            results.getString("username"), results.getString("password"));
                                        break;
                    case "employee":    activeUser = new Employee(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"));
                                        break;
                    case "pharmacist":  activeUser = new Pharmacist(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"));
                        break;
                    default:            return null;
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
     * Function prints a list of active orders to the console
     */

    public void viewOrders(){
        try{
            Statement statement = dbConnection.createStatement();
            ResultSet rslt = statement.executeQuery("SELECT * FROM prescription");
            while (rslt.next()){
                long id = rslt.getLong("id");
                String name = rslt.getString("name");
                long clientId = rslt.getLong("client_id");
                boolean isVal = rslt.getBoolean("is_validated");
                double price = rslt.getDouble("price");

                ResultSet clientStuff = statement.executeQuery("SELECT id, name, username, password, type FROM user where id='" + id + "'");
                Client client = new Client(clientStuff.getLong("id"), clientStuff.getString("name"),
                        clientStuff.getString("username"), clientStuff.getString("password"));

                Order currentOrder = new Order(id,name,client,price);
                System.out.println(currentOrder.toString());
                System.out.println("\n\n");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
