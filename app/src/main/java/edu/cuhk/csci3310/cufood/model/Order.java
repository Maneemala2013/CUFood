package edu.cuhk.csci3310.cufood.model;

import com.google.common.collect.Lists;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Order {
    public static final String FIELD_ADDRESS = "address";
    //public static final String FIELD_DELIVERY_TIME = "deliveryTime";
    public static final String FIELD_FINISHED_COOK_TIME = "finishedCookTime";
    public static final String FIELD_STATUS = "finishedStatus";
    public static final String FIELD_FOOD_NAME = "foodName";
    public static final String FIELD_FOOD_AMOUNT = "foodAmount";
    public static final String FIELD_NOTES = "notes";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_TOTAL_PRICE = "totalPrice";
    public static final String FIELD_USERID = "userId";

    private String address;
    private boolean delivery; // false: pickup, true: delivery
//    private int deliveryTime;
    private Date finishedCookTime;
    private boolean finishedStatus;
    private List<String> foodName;
    private List<Integer> foodAmount;
    private String notes;
    private String riderInfo;
    private @ServerTimestamp Date timestamp;
    private double totalPrice;
    private String userId;
    private String customerOrderId;

    public Order() {}

    public Order(String address, boolean delivery, Date finishedCookTime, boolean finishedStatus, List<String> foodName, List<Integer> foodAmount, String notes, String riderInfo, double totalPrice, String userId, String customerOrderId) {
        this.address = address;
        this.delivery = delivery;
        this.finishedCookTime = finishedCookTime;
        this.finishedStatus = finishedStatus;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.notes = notes;
        this.riderInfo = riderInfo;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.customerOrderId = customerOrderId;
    }

    public String getAddress() { return address; }

    public boolean getDelivery() { return delivery; }

//    public int getDeliveryTime() { return deliveryTime; }
//    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public Date getFinishedCookTime() {
        return finishedCookTime;
    }

    public void setFinishedCookTime(Date finishedCookTime) {
        this.finishedCookTime = finishedCookTime;
    }

    public boolean getFinishedStatus() { return finishedStatus; }

    public List<String> getFoodName() { return foodName; }

    public List<Integer> getFoodAmount() { return foodAmount; }

    public String getNotes() { return notes; }

    public String getRiderInfo() { return riderInfo; }

    public double getTotalPrice() { return totalPrice; }

    public String getUserId() { return userId; }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCustomerOrderId() { return customerOrderId; }

    public void setCustomerOrderId(String customerOrderId) { this.customerOrderId = customerOrderId; }
}
