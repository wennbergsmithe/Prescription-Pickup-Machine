package edu.ithaca.group5;


public class Order {
    long id;
    String name;
    Client client;
    boolean isValidated;
    double price;
    boolean paid;

    Order(long inId, String inName, Client inClient, double inPrice){
        this.id = inId;
        this.name = inName;
        this.client = inClient;
        this.isValidated = false;
        this.price = inPrice;
        this.paid = false;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Client getClient() {
        return client;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public double getPrice() {
        return price;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String orderDetails(){
        String details = "Order ID: " + id + "\n"
                + "Perscription: " + name + "\n"
                + "Client: " + client.name + "\n"
                + "Ready: " + isValidated + "\n"
                + "Price: " + price;
        return details;
    }

    /**
     *Takes in a 'method' string for payment type, and a 'payment' double for the
     * actual payment being made (only used when payment type is in cash)
     * SIDE EFFECTS: A Client object's 'balance' can be added to or subtracted
     * from, and an Order object's 'paid' boolean can be set to true
     * @param method 6
     * @param payment
     */
    public void payOrder(String method, double payment){

        String check = method.toLowerCase();
        double originPrice = price;
        if (isValidated == true){
            if(check.equals("credit") || check.equals("debit")){
                originPrice -= price;
                paid = true;
            } else if (check.equals("cash")){
                originPrice -= payment;
                if (originPrice == 0){
                    paid = true;
                } else if (originPrice < 0){
                    originPrice *= -1;
                    client.addFunds(originPrice);
                    paid = true;
                }
            } else if (check.equals("balance")){
                originPrice -= client.balance;
                if(originPrice == 0){
                    client.balance -= price;
                    paid = true;
                } else if (originPrice < 0){
                    client.balance -= price;
                    paid = true;
                } else if (originPrice > 0){
                    paid = false;
                }
            } else {

            }
        } else {
            paid = false;
        }

    }
}
