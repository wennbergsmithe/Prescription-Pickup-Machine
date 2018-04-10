package edu.ithaca.group5;

import java.util.List;
import java.util.ArrayList;

public class Client extends User {
    List<Order> orders;

    public Client(long id, String name, String username, String password, boolean isFrozen, String allergies) {
        super(id, name, username, password, isFrozen,allergies);
    }

    public Client(long id, String name, String username, String password, String allergies) {
        this(id, name, username, password, false,allergies);
    }

    public void addFunds(double amount){
        balance += amount;
    }

    public String addAllergy(String toAdd){
        if(allergies.contains(toAdd)){
            return toAdd;
        } else {
            allergies += toAdd + ",";
            return allergies;
        }
    }

    public String removeAllergy(String toRemove){
        if(allergies.contains(toRemove)){
            String [] splitString = allergies.split(",");
            for(int i = 0; i < splitString.length; i++){
                if(splitString[i].equals(toRemove)){
                    splitString[i] = null;

                }
            }
            allergies = "";
            for(int i = 0; i < splitString.length; i++){
                allergies += splitString[i];
            }
            return allergies;
        }else {
            return toRemove;
        }

    }

}
