package edu.ithaca.group5;


public class Order {
    long id;
    String name;
    Client client;
    boolean isValidated;
    double price;
    boolean paid;
    String warnings;
    String nextRefill;
    String badCombos = "allergy1&allergy3,allergy2&allergy1,allergy3&allergy4";

    Order(long inId, String inName, Client inClient, double inPrice, String inWarnings, String inNextRefill){
        this.id = inId;
        this.name = inName;
        this.client = inClient;
        this.isValidated = false;
        this.price = inPrice;
        this.paid = false;
        this.warnings = inWarnings;
        this.nextRefill = inNextRefill;
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

    public String getWarnings() {
        return warnings;
    }

    public String getNextRefill() {
        return nextRefill;
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

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public void setNextRefill(String refillDate){
        this.nextRefill = refillDate;
    }

    public String orderDetails(){
        String details = "Order ID: " + id + "\n"
                + "Perscription: " + name + "\n"
                + "Client: " + client.name + "\n"
                + "Ready: " + isValidated + "\n"
                + "Price: " + price + "\n"
                + "DO NOT TAKE IF ALLERGIC TO: " + warnings + "\n"
                + "NEXT REFILL DATE: " + nextRefill;
        return details;
    }

    /**
     *Takes in a 'method' string for payment type, and a 'payment' double for the
     * actual payment being made (only used when payment type is in cash)
     * SIDE EFFECTS: A Client object's 'balance' can be added to or subtracted
     * from, and an Order object's 'paid' boolean can be set to true
     * @param method
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
    
    /**
     * Creates an array of client allergies by splitting it by ','
     * Iterates through array and checks if the array item is contained
     * in the order warnings. If found returns true, else, returns false
     * @return boolean
     */
    public boolean checkAllergies(){
        String[] clientAller = client.allergies.split(",");

        int maxSize = clientAller.length;

        for(int i = 0; i < maxSize; i++){
            //Make sure the split function didn't just return an empty string (in the case of there not being any allergies)
            if (!clientAller[i].equals("")) {
                if (warnings.contains(clientAller[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAgainstOthers(Order secondOrder){
        String[] toCheck = badCombos.split(",");
        String[] orderOne = warnings.split(",");
        String[] orderTwo = secondOrder.warnings.split(",");

        int maxSize;
        int minSize;

        if(orderOne.length > orderTwo.length){
            maxSize = orderOne.length;
            minSize = orderTwo.length;
        }else {
            maxSize = orderTwo.length;
            minSize = orderOne.length;
        }

        for(int i = 0; i < toCheck.length; i++){
            if(!toCheck[i].equals("")){
                for(int x = 0; x < maxSize; i++){
                    for(int y = 0; y < minSize; i++){
                        if((toCheck[i].contains(orderOne[x]))&&(toCheck[i].contains(orderTwo[y]))){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }




}
