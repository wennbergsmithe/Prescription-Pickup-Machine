package edu.ithaca.group5;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen) VALUES ('" + employee.name + "', '" +
                    employee.username + "', '" + employee.password + "', " + "'employee', " + employee.isFrozen + "', '" + employee.allergies +  ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPharmacist(Pharmacist pharmacist) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen) VALUES ('" + pharmacist.name + "', '" +
                    pharmacist.username + "', '" + pharmacist.password + "', " + "'pharmacist', " + pharmacist.isFrozen + "', '" + pharmacist.allergies +  ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addClient(Client client) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen) VALUES ('" + client.name + "', '" +
                    client.username + "', '" + client.password + "', " + "'client', " + client.isFrozen + "', '" + client.allergies +  ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order addOrder(String inName, String username, double inPrice, String inWarnings) {
        return null;
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

    @Override
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
        User user;
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type, isFrozen FROM user where username='" +
                    username + "' and password='" + password + "'");
            if (results.next()) {
                switch (results.getString("type")) {
                    case "client":      user = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"),results.getString("allergies"));
                        break;
                    case "employee":    user = new Employee(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("allergies"));
                        break;
                    case "pharmacist":  user = new Pharmacist(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("allergies"));
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
    public User getUserByUsername(String username) {
        User user;
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type, isFrozen FROM user where username='" +
                    username + "'");
            if (results.next()) {
                switch (results.getString("type")) {
                    case "client":      user = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"),results.getString("allergies"));
                        break;
                    case "employee":    user = new Employee(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("allergies"));
                        break;
                    case "pharmacist":  user = new Pharmacist(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("allergies"));
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
    public int getIDByUsername(String username) {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT id, name, username, password, type FROM user where username='" +
                    username + "'");
            statement.close();

            results.next();
            return results.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orders = new ArrayList<Order>();

        return orders;
    }

    @Override
    public Order getOrderByNameAndUsername(String orderName, String username) {
        return null;
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

    @Override
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();

        try{
            Statement statement = connection.createStatement();
            ResultSet rslt = statement.executeQuery("SELECT * FROM prescription");
            while (rslt.next()){
                long id = rslt.getLong("id");
                String name = rslt.getString("name");
                long clientId = rslt.getLong("client_id");
                boolean isVal = rslt.getBoolean("is_validated");
                double price = rslt.getDouble("price");
                String warnings = rslt.getString("warnings");
                boolean isFrozen = rslt.getBoolean("isFrozen");
                String allergies = rslt.getString("allergies");

                ResultSet clientStuff = statement.executeQuery("SELECT id, name, username, password, type FROM user where id=" + clientId);
                if(clientStuff.next()){
                    Client client = new Client(clientStuff.getLong("id"), clientStuff.getString("name"),
                            clientStuff.getString("username"), clientStuff.getString("password"),isFrozen,allergies);

                    Order currentOrder = new Order(id,name,client,price,warnings);
                    orders.add(currentOrder);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return orders;
    }
}
