package edu.ithaca.group5;

import java.util.List;
import java.util.ArrayList;

public class Client extends User {
    List<Order> discontinuedOrders;
    List<Order> orders;

    public Client(long id, String name, String username, String password, boolean isFrozen, double balance,String allergies) {
        super(id, name, username, password, isFrozen,balance,allergies);

    }

    public Client(long id, String name, String username, String password, double balance, boolean isFrozen, String salt, String allergies) {
        super(id, name, username, password, isFrozen, salt, balance, allergies);
    }

    public Client(long id, String name, String username, String password, boolean isFrozen) {
        this(id, name, username, password, isFrozen, 0, "");
    }

    public void addFunds(double amount){
        balance += amount;
    }
    /**
     *  Takes in a String of an allergy to be added to 'allergies' String.
     *  If the allergy is already in the String, 'toAdd' is returned. If
     *  it is not in the String already then it is added to the String
     *  and 'allergies' is returned
     * @param toAdd String to be added to the array
     * @return String
     */
    public String addAllergy(String toAdd){
        if(allergies.contains(toAdd)){
            return toAdd;
        } else {
            allergies += "," + toAdd;
            return allergies;
        }
    }

    /**
     * Takes in a String of an allergy to be removed from 'allergies'.
     * If it is not in the string then 'toRemove' is returned. If it
     * is in the String then the String is split into an array. The 'toRemove'
     * String is found, made null, and the remaining items are
     * added back to the array. 'allergies' is then returned.
     * @param toRemove String to be removed
     * @return String
     */
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
    /**
     * Takes in a String of an order name to be removed
     * If it is not in the string then the boolean value false is returned. If it
     * is in the String then the order is removed and true is then returned.
     * @param thePPM PPM where order is
     * @param orderName Order to be removed.
     * @return Boolean
     */
    public boolean discontinueOrder(PPM thePPM, String orderName){
        boolean returnedOrder = false;
        for (int i = 0; i < orders.size(); i++){
            if (orders.get(i).name.compareTo(orderName) == 0){
                orders.remove(i);
                break;
            }
        }
        for (int o = 0; o < thePPM.loadedOrders.size(); o++){
            if (thePPM.loadedOrders.get(o).name.compareTo(orderName) == 0){
                discontinuedOrders.add(thePPM.loadedOrders.get(0));
                thePPM.loadedOrders.remove(o);
                returnedOrder = true;
                break;
            }
        }
        return returnedOrder;
    }
}
