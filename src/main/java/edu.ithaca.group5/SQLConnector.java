package edu.ithaca.group5;

import javax.swing.plaf.nimbus.State;
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
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt, allergies, balance) VALUES ('" + employee.name + "', '" +
                    employee.username + "', '" + employee.password + "', " + "'employee', " + employee.isFrozen +  ", '" + employee.passwordSalt + "', '" + employee.allergies + "', " + employee.balance +")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPharmacist(Pharmacist pharmacist) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt, allergies, balance) VALUES ('" + pharmacist.name + "', '" +
                    pharmacist.username + "', '" + pharmacist.password + "', " + "'pharmacist', " + pharmacist.isFrozen +  ", '" + pharmacist.passwordSalt + "', '" + pharmacist.allergies + "', " + pharmacist.balance +")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addClient(Client client) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user (name, username, password, type, isFrozen, salt, allergies, balance) VALUES ('" + client.name + "', '" +
                    client.username + "', '" + client.password + "', " + "'client', " + client.isFrozen +  ", '" + client.passwordSalt + "', '" + client.allergies + "', " + client.balance +")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order addOrder(String inName, String username, double inPrice, String inWarnings,String inRefillDate, boolean easyOpen) {
        long userid = getIDByUsername(username);
        try {
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO prescription (name, client_id, is_validated, price, paid, warnings,nextRefill) VALUES ('" + inName + "', " + userid + ", 0, " + inPrice + ", 0, '" + inWarnings + "', '"+ inRefillDate + "')";
            statement.execute(sql);
            return getOrderByNameAndUsername("inName", "username");

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean isInDB(Client toCheck){
        boolean isThere;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT 1 FROM user WHERE (name, username, type) VALUES ('" + toCheck.name + "', '" +
                    toCheck.username + "', 'client'";
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
            String sql = "DELETE FROM user (name, username, type) VALUES ('" + clientToRemove.name + "', '" +
                    clientToRemove.username + "', 'client')";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientToRemove;
    }

    @Override
    public boolean returnOrder(Order order) {
        try {

            Statement checkState = connection.createStatement();
            String checkSql = "SELECT * FROM prescription WHERE id=" + order.id + " and paid=0";
            ResultSet results = checkState.executeQuery(checkSql);
            if (!results.next()) return false;



            Statement statement = connection.createStatement();
            String sql = "UPDATE prescription SET paid=0 WHERE id=" + order.id;
            statement.execute(sql);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void setPaidTrue(Order order) {
        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE prescription SET paid=1 WHERE id=" + order.id;
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            ResultSet results = statement.executeQuery("SELECT * FROM user where username='" +
                    username + "'");
            if (results.next()) {
                switch (results.getString("type")) {
                    case "client":  user = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getDouble("balance"), results.getBoolean("isFrozen"),
                            results.getString("salt"), results.getString("allergies"));
                    break;

                    case "employee": user = new Employee(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"), results.getString("salt"),
                            results.getDouble("balance"),results.getString("allergies"));
                    break;

                    case "pharmacist": user = new Pharmacist(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getBoolean("isFrozen"),results.getDouble("balance"),
                            results.getString("salt"),results.getString("allergies"));

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


            results.next();
            int row = results.getRow();
            statement.close();
            return row;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        List<Order> orders = new ArrayList<Order>();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            int id = getIDByUsername(username);

            ResultSet rslt = statement.executeQuery("SELECT * FROM prescription WHERE client_id=" + id);
            while(rslt.next()){
                long orderId = rslt.getLong("id");
                String name = rslt.getString("name");
                long clientId = rslt.getLong("client_id");
                boolean isVal = rslt.getBoolean("is_validated");
                double price = rslt.getDouble("price");
                String warnings = rslt.getString("warnings");
                String refillDate = rslt.getString("nextRefill");
                boolean easyOpen = rslt.getBoolean("easy_open");

                ResultSet results = statement.executeQuery("SELECT * FROM user where id=" + clientId);
                if(results.next()){
                    Client client = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getDouble("balance"), results.getBoolean("isFrozen"),
                            results.getString("salt"), results.getString("allergies"));


                    Order currentOrder = new Order(id,name,client,price,warnings,refillDate, easyOpen);

                    orders.add(currentOrder);
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Order getOrderByNameAndUsername(String orderName, String username) {

        Statement statement = null;

        try{
            statement = connection.createStatement();
            int id = getIDByUsername(username);

            if(id != -1){
                ResultSet resultSet = statement.executeQuery("SELECT * FROM prescription WHERE client_id=" + id + " AND name='" + orderName + "'");

                Statement statement1 = connection.createStatement();

                ResultSet results = statement1.executeQuery("SELECT * FROM user where id=" + id);
                Client client;

                if(results.next()) {
                    client = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getDouble("balance"), results.getBoolean("isFrozen"),
                            results.getString("salt"), results.getString("allergies"));
                }else{
                    statement.close();
                    return null;
                }
                if (resultSet.next()){
                    long orderId = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    String warnings = resultSet.getString("warnings");
                    String refillDate = resultSet.getString("nextRefill");
                    boolean easyOpen = resultSet.getBoolean("easy_open");
                    Order toReturn = new Order(orderId,name,client,price,warnings,refillDate, easyOpen);
                    statement.close();
                    return toReturn;
                }
            }else{
                statement.close();
                return null;
            }
        }catch (java.sql.SQLException e){
            e.printStackTrace();
        }
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
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unfreezeUser(User user) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user SET isFrozen = FALSE where id = " + user.id);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePassword(User user) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user SET password = '" + user.password + "', salt = '" + user.passwordSalt + "' WHERE id = " + user.id);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEasyOpen(Order order, boolean newBool) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE prescription SET easy_open = "+ newBool +" where id = " + order.id);
            statement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void updateBalance(User user) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user SET balance = " + user.balance + " WHERE id = " + user.id);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void validateAllOrders() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE prescription SET is_validated=1");

            statement.close();
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
                String refillDate = rslt.getString("nextRefill");
                boolean isFrozen = rslt.getBoolean("isFrozen");
                String allergies = rslt.getString("allergies");
                double balance = rslt.getDouble("balance");
                boolean easyOpen = rslt.getBoolean("easy_open");

                Statement statement1 = connection.createStatement();
                ResultSet results = statement1.executeQuery("SELECT * FROM user where id=" + clientId);
                if(results.next()){
                    Client client = new Client(results.getLong("id"), results.getString("name"),
                            results.getString("username"), results.getString("password"),
                            results.getDouble("balance"), results.getBoolean("isFrozen"),
                            results.getString("salt"), results.getString("allergies"));

                    Order currentOrder = new Order(id,name,client,price,warnings,refillDate, easyOpen);
                    currentOrder.isValidated = isVal;
                    orders.add(currentOrder);
                }
            }
            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return orders;
    }


}
