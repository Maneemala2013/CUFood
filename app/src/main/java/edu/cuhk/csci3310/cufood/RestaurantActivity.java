package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantMenu";

    private static String restaurantId;
    private String customerId;

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_USER_ID = "key_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);

        // Get customer ID from extras
        customerId = getIntent().getExtras().getString(KEY_USER_ID);
        if (customerId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_USER_ID);
        }

        // Get restaurant ID from extras
        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }
    }
    public void onRatingClicked(View view) {
        // Go to the rating page of the selected restaurant
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
        startActivity(intent);
    }

    public void onOrderClicked(View view) {
        // Go to the order page of the selected restaurant
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
        startActivity(intent);
    }

    public void onMenuClicked(View view) {
        //TODO
        Intent intent = new Intent(this, MenuCategoryActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
        startActivity(intent);
    }

    public void onMenuCustomerClicked(View view) {

        Intent intent = new Intent(this, MenuCustomerActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
        intent.putExtra(RestaurantActivity.KEY_USER_ID, customerId);
        startActivity(intent);
    }
}