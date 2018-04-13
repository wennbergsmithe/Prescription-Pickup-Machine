package edu.ithaca.group5;

import java.sql.*;

public class SQLConnector implements DBConnector {
    Connection connection;

    public SQLConnector() throws SQLException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        connection = DriverManager.getConnection(Config.DB_HOST, Config.DB_USER, Config.DB_PASSWORD);
    }

    @Override
    public void addEmployee(Employee employee) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt) VALUES ('" + employee.name + "', '" +
                    employee.username + "', '" + employee.password + "', " + "'employee', " + employee.isFrozen +  ", '" + employee.passwordSalt + "')");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPharmacist(Pharmacist pharmacist) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt) VALUES ('" + pharmacist.name + "', '" +
                    pharmacist.username + "', '" + pharmacist.password + "', " + "'pharmacist', " + pharmacist.isFrozen + ", '" + pharmacist.passwordSalt +   "')");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addClient(Client client) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt) VALUES ('" + client.name + "', '" +
                    client.username + "', '" + client.password + "', " + "'client', " + client.isFrozen + ", '" + client.passwordSalt +  "')");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isInDB(Client toCheck){
        boolean isThere;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT 1 FROM user WHERE (name, username, password, type) VALUES ('" + toCheck.name + "', '" +
                    toCheck.username + "', '" + toCheck.password + "', " + "'client'";
            ResultSet results = statement.executeQuery(sql);

            if (!results.next()){
                statement.close();
                return false;
            }else{
                statement.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Client removeClient(Client clientToRemove){
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM user (name, username, password, type) VALUES ('" + clientToRemove.name + "', '" +
                    clientToRemove.username + "', '" + clientToRemove.password + "', " + "'client')";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientToRemove;
    }


    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.isPassword(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        User user;
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type, isFrozen, salt FROM user where username='" +
                    username + "'");
            if (results.next()) {
                switch (results.getString("type")) {
                    case "client":      user = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("salt"));
                        break;
                    case "employee":    user = new Employee(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("salt"));
                        break;
                    case "pharmacist":  user = new Pharmacist(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("salt"));
                        break;
                    default:            return null;
                }
                statement.close();
                return user;

            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void emptyUserTable() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE user");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void emptyOrderTable() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE prescription");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void freezeUser(User user) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user SET isFrozen = TRUE where id = " + user.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unfreezeUser(User user) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user SET isFrozen = FALSE where id = " + user.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
