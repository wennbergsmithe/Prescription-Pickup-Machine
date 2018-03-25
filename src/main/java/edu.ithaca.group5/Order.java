package edu.ithaca.group5;

public class Order {
    long id;
    String name;
    Client client;
    boolean isValidated;
    double price;

    Order(long inId, String inName, Client inClient, double inPrice){
        this.id = inId;
        this.name = inName;
        this.client = inClient;
        this.isValidated = false;
        this.price = inPrice;
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
}
