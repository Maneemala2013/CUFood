package edu.cuhk.csci3310.cufood.model;

public class CanteenManager {
    private String canteenID;
    private String password;
    private String restaurantId;
    public CanteenManager() {}
    public CanteenManager(String canteenID, String password, String restaurantId) {
        this.canteenID = canteenID;
        this.password = password;
        this.restaurantId = restaurantId;
    }
    public String getCanteenID() {
        return canteenID;
    }
    public void setCanteenID(String canteenID) {
        this.canteenID = canteenID;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {this.password= password;}
    public String getRestaurantId() {return restaurantId;}
    public void setRestaurantId() {this.restaurantId = restaurantId;}
}
