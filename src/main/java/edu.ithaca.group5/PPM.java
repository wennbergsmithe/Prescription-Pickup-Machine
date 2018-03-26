package edu.ithaca.group5;

import java.sql.*;

public class PPM {
    Connection dbConnection;
    User activeUser;

    public PPM(String dbHost, String dbUser, String dbPassword) throws SQLException {
        // needed to register db driver
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        dbConnection = DriverManager.getConnection(dbHost, dbUser, dbPassword);
    }

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
}
